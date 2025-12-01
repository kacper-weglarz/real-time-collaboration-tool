package io.github.kacper.weglarz.realtimecollaboration.controller;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.LoginRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.RegisterRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.AuthResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user
     * @param request DTO -> username and password
     * @return HTTP 200 OK
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.authenticate(request);

        return  ResponseEntity.ok(response);
    }

    /**
     * Registers a new user
     * @param request DTO -> username, email and password
     * @return HTTP 200 OK
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
