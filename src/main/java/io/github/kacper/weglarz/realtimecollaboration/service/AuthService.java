package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.LoginRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.RegisterRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.AuthResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UnauthorizedAccessException;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UserAlreadyExistsException;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UserNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.repository.UserRepository;
import io.github.kacper.weglarz.realtimecollaboration.security.jwt.JWTService;
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
    private final JWTService jwtService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,  JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates an existing user based on username and password
     * @param request DTO -> username and password
     * @return authentication response with user info and message
     */
    public AuthResponseDTO authenticate(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedAccessException("Passwords don't match");
        }

        return new AuthResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jwtService.generateToken(user),
                "Login successful!"
        );
    }

    /**
     * Registers a new user if username and email are unique
     * @param request DTO -> username, email and password
     * @return registration response with created user info and message
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists" + request.getUsername());
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists" +  request.getEmail());
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
