package com.goaltracker.security;

import com.goaltracker.domain.User;
import com.goaltracker.repository.UserRepository;
import jakarta.ws.rs.NotAuthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public record LoginResult(String token, long expiresInMinutes, Long userId, String username, String displayName, String role) {
    }

    public LoginResult login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()))
                .orElseThrow(() -> new NotAuthorizedException("Bearer"));

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new LoginResult(token, jwtTokenProvider.getExpirationMinutes(), user.getId(), user.getUsername(),
                user.getDisplayName(), user.getRole().name());
    }
}
