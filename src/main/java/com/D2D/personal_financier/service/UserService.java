package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.jwt.JwtProvider;
import com.D2D.personal_financier.dto.authDTO.AuthResponseDto;
import com.D2D.personal_financier.dto.authDTO.RegisterRequestDto;
import com.D2D.personal_financier.dto.userDTO.UserRequestDto;
import com.D2D.personal_financier.dto.userDTO.UserResponseDto;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.mapper.UserMapper;
import com.D2D.personal_financier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }


        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);

        String emailToken = emailVerificationService.generateToken(user);
        emailService.sendVerificationEmail(user.getEmail(), emailToken);

        String token = jwtProvider.generateToken(user.getUsername());
        return new AuthResponseDto(token);
    }

    public AuthResponseDto login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtProvider.generateToken(user.getUsername());
        return new AuthResponseDto(token);
    }


    public UserResponseDto createUser(UserRequestDto dto) {
        User user = userMapper.toEntity(dto);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

