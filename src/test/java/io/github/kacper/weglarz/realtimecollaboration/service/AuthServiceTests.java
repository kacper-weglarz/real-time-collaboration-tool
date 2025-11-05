package io.github.kacper.weglarz.realtimecollaboration.service;


import io.github.kacper.weglarz.realtimecollaboration.dto.request.LoginRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.RegisterRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.AuthResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    /**
     * Test if register method can successfuly register a user
     */
    @Test
    public void test_register_successful() {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("user");
        request.setEmail("user@email.com");
        request.setPassword("1234");

         when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
         when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.empty());

        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setUsername("user");
        user.setPasswordHash("hashed1234");

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponseDTO response = authService.register(request);

        assertEquals(1L, response.getUserId());
        assertEquals("user", response.getUsername());
        assertEquals("user@email.com", response.getEmail());
        assertEquals("", response.getToken());
        assertEquals("Registration successful!", response.getMessage());

        verify(userRepository).save(any(User.class));
    }

    /**
     * Test if register method can not register a user because of "Username already exists"
     */
    @Test
    public void test_register_username_already_exists() {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("user");
        request.setEmail("user@email.com");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("user@email.com");
        existingUser.setUsername("user");
        existingUser.setPasswordHash("hashed1234");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existingUser));

        RuntimeException  exception = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertEquals("Username already exists", exception.getMessage());


    }

    /**
     * Test if register method can not register a user because of "Email already exists"
     */
    @Test
    public void test_register_email_already_exists() {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("user");
        request.setEmail("user@email.com");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("user");
        existingUser.setEmail("user@email.com");
        existingUser.setPasswordHash("hashed1234");

        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(existingUser));

        RuntimeException  exception = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email already exists", exception.getMessage());


    }

    /**
     * Test if authenticate method can successfuly log in a user
     */
    @Test
    public void test_authenticate_successful() {

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("user");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("user");
        existingUser.setEmail("user@email.com");
        existingUser.setPasswordHash("hashed1234");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("1234","hashed1234")).thenReturn(true);

        AuthResponseDTO response = authService.authenticate(request);

        assertEquals(1L, response.getUserId());
        assertEquals("user",  response.getUsername());
        assertEquals("user@email.com", response.getEmail());
        assertEquals("",  response.getToken());
        assertEquals("Login successful!", response.getMessage());
    }

    /**
     * Test if authenticate method can not log in user with username not found
     */
    @Test
    public void test_authenticate_username_not_found() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("user");
        request.setPassword("1234");

        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(request);
        });

        assertEquals("Username not found", exception.getMessage());
    }

    /**
     * Test if authenticate method can not log in user with wrong password
     */
    @Test
    public void test_authenticate_password_mismatch() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("user");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("user");
        existingUser.setEmail("user@email.com");
        existingUser.setPasswordHash("wrong-password");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("1234","wrong-password")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(request);
        });

        assertEquals("Passwords don't match", exception.getMessage());
    }
}
