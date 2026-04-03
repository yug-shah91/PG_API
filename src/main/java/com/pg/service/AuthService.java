package com.pg.service;

import com.pg.dto.request.LoginRequest;
import com.pg.dto.request.RegisterRequest;
import com.pg.dto.response.AuthResponse;
import com.pg.entity.User;
import com.pg.entity.enums.Role;
import com.pg.repository.UserRepository;
import com.pg.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        if (request.getRole() == Role.TENANT) {
            throw new RuntimeException("Tenants are registered by the owner. Contact your PG owner.");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : Role.OWNER)
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token).name(user.getName())
                .email(user.getEmail()).role(user.getRole().name())
                .message("Registration successful").build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account with email: " + request.getEmail()));
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated. Contact support.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token).name(user.getName())
                .email(user.getEmail()).role(user.getRole().name())
                .message("Login successful").build();
    }
}
