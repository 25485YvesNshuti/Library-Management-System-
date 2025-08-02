package com.Library.service;

import com.Library.dto.UserDTO;
import com.Library.model.User;
import com.Library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDTO);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public long countByRole(String role) {
        if (role == null) throw new IllegalArgumentException("Role cannot be null");
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            return userRepository.countByRole(userRole);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        user.setId(null); // Ensure ID is not set for new user
        // Set defaults if needed: enabled, createdAt etc.
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public Optional<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(userDTO.getName());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setRole(User.Role.valueOf(userDTO.getRole()));
            existingUser.setEnabled(userDTO.getEnabled());
            existingUser.setPhone(userDTO.getPhone());
            // Do not update password or relationships here
            return toDTO(userRepository.save(existingUser));
        });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Mapping methods

    private UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.getEnabled())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User toEntity(UserDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .role(User.Role.valueOf(dto.getRole()))
                .enabled(dto.getEnabled())
                .phone(dto.getPhone())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}