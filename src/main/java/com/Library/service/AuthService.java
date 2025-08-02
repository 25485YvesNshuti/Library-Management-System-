package com.Library.service;

import com.Library.model.User;
import com.Library.repository.UserRepository;
import com.Library.dto.*;
import com.Library.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthResponse login(AuthRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    if (!user.getEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid email or password");
    }
    // Use userId for JWT subject
    String token = jwtUtil.generateToken(user.getId(), user.getRole().name());
    return AuthResponse.builder()
            .token(token)
            .user(toDto(user))
            .build();
}

    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? User.Role.valueOf(request.getRole().toUpperCase()) : User.Role.STUDENT)
                .enabled(true)
                .phone(request.getPhone())
                .createdAt(java.time.LocalDateTime.now())
                .build();
        user = userRepository.save(user);
        return toDto(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            // Generate a reset token (e.g., UUID or JWT)
            String resetToken = jwtUtil.generateResetToken(userOpt.get().getEmail());
            String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
            emailService.sendEmail(userOpt.get().getEmail(), "Reset Password", 
                "Reset your password: " + resetLink);
        }
        // Always respond with success for security
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtUtil.extractEmailFromResetToken(request.getToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserDTO toDto(User user) {
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
}