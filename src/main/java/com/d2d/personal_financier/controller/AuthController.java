package com.d2d.personal_financier.controller;

import com.d2d.personal_financier.config.security.utils.JwtBlacklistService;
import com.d2d.personal_financier.dto.LoginDto.LoginRequestDto;
import com.d2d.personal_financier.dto.authDTO.AuthResponseDto;
import com.d2d.personal_financier.dto.authDTO.RegisterRequestDto;
import com.d2d.personal_financier.dto.error.ErrorResponse;
import com.d2d.personal_financier.dto.message.MessageResponseDto;
import com.d2d.personal_financier.dto.tokenDTO.ResendVerificationRequestDto;
import com.d2d.personal_financier.service.EmailVerificationService;
import com.d2d.personal_financier.service.UserService;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final EmailVerificationService emailVerificationService;
    private final JwtBlacklistService jwtBlacklistService;
    private final UserService userService;

    private static final String REGISTER = "/register";
    private static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";
    private static final String RESEND = "/resend-verification";
    private static final String VERIFY = "/verify-email";

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
        summary = "Logout user",
        description = "Invalidate user session on client side"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User logged out successfully")
    })
    @PostMapping(LOGOUT)
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            jwtBlacklistService.blacklistToken(token);
        }

        return ResponseEntity.noContent().build();
    }

}
