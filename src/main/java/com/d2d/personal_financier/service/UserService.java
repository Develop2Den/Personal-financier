package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.config.security.utils.LoginAttemptService;
import com.d2d.personal_financier.dto.auth_dto.AuthResponseDto;
import com.d2d.personal_financier.dto.auth_dto.RegisterRequestDto;
import com.d2d.personal_financier.dto.message.MessageResponseDto;
import com.d2d.personal_financier.dto.user_dto.UserRequestDto;
import com.d2d.personal_financier.dto.user_dto.UserResponseDto;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.EmailNotVerifiedException;
import com.d2d.personal_financier.exception.InvalidCredentialsException;
import com.d2d.personal_financier.exception.EmailAlreadyRegisteredException;
import com.d2d.personal_financier.exception.TooManyAttemptsException;
import com.d2d.personal_financier.exception.UserNotFoundException;
import com.d2d.personal_financier.exception.UsernameAlreadyTakenException;
import com.d2d.personal_financier.mapper.UserMapper;
import com.d2d.personal_financier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EmailVerificationService emailVerificationService;
    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;

    public MessageResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyTakenException(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException(request.email());
        }


        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);

        String emailToken = emailVerificationService.generateToken(user);
        emailService.sendVerificationEmail(user.getEmail(), emailToken);
        auditService.log("REGISTER", "SUCCESS", user, user.getUsername(), "User registered");

        return new MessageResponseDto(
                "Registration successful. Please check your email and verify it before logging in."
        );
    }

    public AuthResponseDto login(String username, String password) {

        if (loginAttemptService.isBlocked(username)) {
            auditService.log("LOGIN", "BLOCKED", null, username, "Too many login attempts");
            throw new TooManyAttemptsException();
        }

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                loginAttemptService.loginFailed(username);
                auditService.log("LOGIN", "FAILED", null, username, "Unknown username");
                return new InvalidCredentialsException();
            });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(username);
            auditService.log("LOGIN", "FAILED", user, username, "Invalid password");
            throw new InvalidCredentialsException();
        }

        if (!Boolean.TRUE.equals(user.getVerified())) {
            auditService.log("LOGIN", "DENIED", user, username, "Email not verified");
            throw new EmailNotVerifiedException();
        }

        loginAttemptService.loginSucceeded(username);
        auditService.log("LOGIN", "SUCCESS", user, username, "Login successful");

        return refreshTokenService.createTokenPair(user);
    }

    public UserResponseDto getCurrentUser() {
        User currentUser = securityUtils.getCurrentUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException(currentUser.getId()));

        return userMapper.toDto(user);
    }

    public UserResponseDto getUserById(Long id) {
        User user = getCurrentUserById(id);

        return userMapper.toDto(user);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = getCurrentUserById(id);

        validateUniqueUserFields(dto.username(), dto.email(), user.getId());

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        User user = getCurrentUserById(id);
        userRepository.delete(user);
    }

    private User getCurrentUserById(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        if (!currentUser.getId().equals(id)) {
            throw new UserNotFoundException(id);
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void validateUniqueUserFields(String username, String email, Long userId) {
        boolean usernameTaken = userId == null
                ? userRepository.existsByUsername(username)
                : userRepository.existsByUsernameAndIdNot(username, userId);

        if (usernameTaken) {
            throw new UsernameAlreadyTakenException(username);
        }

        boolean emailTaken = userId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, userId);

        if (emailTaken) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }
}
