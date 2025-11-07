package io.github.kacper.weglarz.realtimecollaboration.dto.request;

import lombok.Data;

@Data
public class UserProfileDTO {

    private Long id;
    private String username;
    private String email;
}
