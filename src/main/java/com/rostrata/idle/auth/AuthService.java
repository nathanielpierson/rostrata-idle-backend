package com.rostrata.idle.auth;

import com.rostrata.idle.auth.dto.RegistrationRequest;
import com.rostrata.idle.auth.dto.UserResponse;
import com.rostrata.idle.user.User;
import com.rostrata.idle.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegistrationRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email is already in use");
        });
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username is already in use");
        });

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail().trim().toLowerCase(),
                request.getUsername().trim(),
                hashedPassword
        );

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getWoodcuttingXp(),
                user.getFishingXp(),
                user.getMiningXp(),
                user.getHuntingXp()
        );
    }
}

