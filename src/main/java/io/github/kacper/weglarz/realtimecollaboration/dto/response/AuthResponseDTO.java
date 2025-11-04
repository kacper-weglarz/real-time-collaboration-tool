package io.github.kacper.weglarz.realtimecollaboration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private Long userId;
    private String username;
    private String email;
    private String token;
    private String message;
}
