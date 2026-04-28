package com.d2d.personal_financier.controller;

import com.d2d.personal_financier.config.security.utils.JwtBlacklistService;
import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import com.d2d.personal_financier.dto.auth_dto.LogoutRequestDto;
import com.d2d.personal_financier.dto.auth_dto.PasswordResetConfirmDto;
import com.d2d.personal_financier.dto.auth_dto.PasswordResetRequestDto;
import com.d2d.personal_financier.dto.auth_dto.RefreshTokenRequestDto;
import com.d2d.personal_financier.dto.login_dto.LoginRequestDto;
import com.d2d.personal_financier.dto.auth_dto.AuthResponseDto;
import com.d2d.personal_financier.dto.auth_dto.RegisterRequestDto;
import com.d2d.personal_financier.dto.error.ErrorResponse;
import com.d2d.personal_financier.dto.message.MessageResponseDto;
import com.d2d.personal_financier.dto.token_dto.ResendVerificationRequestDto;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.repository.UserRepository;
import com.d2d.personal_financier.service.EmailVerificationService;
import com.d2d.personal_financier.service.AuditService;
import com.d2d.personal_financier.service.PasswordResetService;
import com.d2d.personal_financier.service.RefreshTokenService;
import com.d2d.personal_financier.service.UserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final EmailVerificationService emailVerificationService;
    private final JwtBlacklistService jwtBlacklistService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final AuditService auditService;

    private static final String REGISTER = "/register";
    private static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";
    private static final String REFRESH = "/refresh";
    private static final String RESEND = "/resend-verification";
    private static final String VERIFY = "/verify-email";
    private static final String PASSWORD_RESET = "/password-reset";
    private static final String PASSWORD_RESET_CONFIRM = "/password-reset/confirm";

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and returns an authentication token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username already taken: {username} or email already registered: {email}",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(REGISTER)
    public ResponseEntity<MessageResponseDto> register(
        @Valid @RequestBody RegisterRequestDto request) {

        MessageResponseDto response = userService.register(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @Operation(
        summary = "Verify user email",
        description = "Verifies email using verification token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid email verification token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email verification token has already been used",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "410",
            description = "Email verification token has expired",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping(VERIFY)
    public ResponseEntity<MessageResponseDto> verifyEmail(
        @RequestParam String token) {

        emailVerificationService.verifyToken(token);

        return ResponseEntity.ok(
            new MessageResponseDto("Email verified successfully!")
        );
    }

    @Operation(
        summary = "Resend verification email",
        description = "Sends a new email verification link if the account exists and is not yet verified"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Verification email status returned"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(RESEND)
    public ResponseEntity<MessageResponseDto> resendVerificationEmail(
        @Valid @RequestBody ResendVerificationRequestDto request) {

        return ResponseEntity.ok(
            emailVerificationService.resendVerificationEmail(request.email())
        );
    }

    @Operation(
        summary = "Login user",
        description = "Authenticate user and return JWT token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User logged in successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid username or password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Please verify your email before logging in",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Too many login attempts. Try again later.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(LOGIN)
    public ResponseEntity<AuthResponseDto> login(
        @Valid @RequestBody LoginRequestDto request) {

        return ResponseEntity.ok(
            userService.login(request.username(), request.password())
        );
    }

    @Operation(
        summary = "Refresh access token",
        description = "Rotates the refresh token and returns a new token pair"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token pair refreshed successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token is invalid, revoked or expired",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(REFRESH)
    public ResponseEntity<AuthResponseDto> refresh(
        @Valid @RequestBody RefreshTokenRequestDto request) {

        return ResponseEntity.ok(
            refreshTokenService.refresh(request.refreshToken())
        );
    }

    @Operation(
        summary = "Logout user",
        description = "Invalidate user session on client side"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User logged out successfully")
    })
    @PostMapping(LOGOUT)
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        @RequestBody(required = false) LogoutRequestDto logoutRequest) {

        String authHeader = request.getHeader("Authorization");
        String username = null;
        User user = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            Optional<Claims> claims = jwtProvider.getValidatedClaims(token);

            if (claims.isPresent()) {

                username = claims.get().getSubject();
                user = userRepository.findByUsername(username).orElse(null);

                jwtBlacklistService.blacklistToken(token);
            }
        }

        if (logoutRequest != null && logoutRequest.refreshToken() != null) {
            refreshTokenService.revoke(logoutRequest.refreshToken());
        }

        auditService.log("LOGOUT", "SUCCESS", user, username, "Logout completed");

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Request password reset",
        description = "Sends password reset instructions if the account exists"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password reset request accepted"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(PASSWORD_RESET)
    public ResponseEntity<MessageResponseDto> requestPasswordReset(
        @Valid @RequestBody PasswordResetRequestDto request) {

        return ResponseEntity.ok(
            passwordResetService.requestReset(request.email())
        );
    }

    @Operation(
        summary = "Confirm password reset",
        description = "Sets a new password using a valid password reset token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password reset completed"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid reset token or password policy violation",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Reset token has already been used",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "410",
            description = "Reset token has expired",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(PASSWORD_RESET_CONFIRM)
    public ResponseEntity<MessageResponseDto> confirmPasswordReset(
        @Valid @RequestBody PasswordResetConfirmDto request) {

        return ResponseEntity.ok(
            passwordResetService.confirmReset(request)
        );
    }

}
