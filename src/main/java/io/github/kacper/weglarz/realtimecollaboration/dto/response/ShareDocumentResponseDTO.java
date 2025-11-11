package io.github.kacper.weglarz.realtimecollaboration.dto.response;

import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDocumentResponseDTO {

    private Long documentId;
    private Role sharedWithRole;
    private Long sharedToUserId;

}
