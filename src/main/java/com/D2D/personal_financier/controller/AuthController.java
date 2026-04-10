package com.D2D.personal_financier.controller;

import com.D2D.personal_financier.dto.authDTO.AuthResponseDto;
import com.D2D.personal_financier.dto.authDTO.RegisterRequestDto;
import com.D2D.personal_financier.service.EmailVerificationService;
import com.D2D.personal_financier.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    private static final String REGISTER = "/register";
    private static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";
    private static final String VERIFY = "/verify-email";

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns an authentication token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping(REGISTER)
    public ResponseEntity<AuthResponseDto> register(
            @RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping(VERIFY)
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyToken(token);
        return ResponseEntity.ok("Email verified successfully!");
    }


    @Operation(summary = "Login user", description = "Authenticate user with username and password, returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping(LOGIN)
    public ResponseEntity<AuthResponseDto> login(
            @RequestParam String username,
            @RequestParam String password) {
        return ResponseEntity.ok(userService.login(username, password));
    }

    @Operation(summary = "Logout user", description = "Invalidate user session on client side")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User logged out successfully")
    })
    @PostMapping(LOGOUT)
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

}
