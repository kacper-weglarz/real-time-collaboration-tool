package io.github.kacper.weglarz.realtimecollaboration.dto.request;

import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import lombok.Data;

@Data
public class ShareDocumentRequestDTO {

    private Role role;
    private String username;

}
