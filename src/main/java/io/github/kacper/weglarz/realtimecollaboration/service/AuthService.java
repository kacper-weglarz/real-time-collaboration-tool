package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.LoginRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.RegisterRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.AuthResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user authentication and registration
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates an existing user based on username and password
     *
     * @param request DTO containing username and password
     * @return authentication response with user info and message
     * @throws RuntimeException if username not found or password is invalid
     */
    public AuthResponseDTO authenticate(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Passwords don't match");
        }

        return new AuthResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "",
                "Login successful!"
        );
    }

    /**
     * Registers a new user if username and email are unique.
     *
     * @param request DTO containing username, email, and password
     * @return registration response with created user info and message
     * @throws RuntimeException if username or email already exist
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);

        User savedUser = userRepository.save(user);

        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                "",
                "Registration successful!"
        );
    }
}
