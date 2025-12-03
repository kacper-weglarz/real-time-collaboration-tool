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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void should_Authenticate_User() {

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("testUser");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPasswordHash("hashedPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("JWTtoken");

        AuthResponseDTO response = authService.authenticate(request);

        assertEquals("JWTtoken", response.getToken());
        assertEquals("testUser", response.getUsername());
    }

    @Test
    void should_Throw_UserNotFoundException_When_Login_UsernameDoesNotExist() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("unknownUser");

        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.authenticate(request));
    }

    @Test
    void should_Throw_UnauthorizedAccessException_When_PasswordIsIncorrect() {

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("testUser");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setPasswordHash("hashedPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> authService.authenticate(request));
    }

    @Test
    void should_Register_User() {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("newUser");
        request.setEmail("newUser@email.com");
        request.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newUser");
        savedUser.setEmail("newUser@email.com");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newUser@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponseDTO response = authService.register(request);

        assertEquals("newUser", response.getUsername());
        assertEquals("Registration successful!", response.getMessage());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void should_Throw_UserAlreadyExistsException_When_Register_UsernameAlreadyExists() {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("existingUser");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_Throw_UserAlreadyExistsException_When_Register_EmailAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("newUser");
        request.setEmail("existing@email.com");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@email.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }
}