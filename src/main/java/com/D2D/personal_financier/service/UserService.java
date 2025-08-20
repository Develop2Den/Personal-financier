package com.D2D.personal_financier.service;

import com.D2D.personal_financier.dto.userDTO.UserRequestDto;
import com.D2D.personal_financier.dto.userDTO.UserResponseDto;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.mapper.UserMapper;
import com.D2D.personal_financier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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

