package io.github.kacper.weglarz.realtimecollaboration.dto.response;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String ownerUsername;
    private Role currentUserRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Konstruktor do tworzenia DocumentResponseDTO
     * @param doc przyjmuje jako parametr dokument
     */
    public DocumentResponseDTO(Document doc) {
        this.id = doc.getId();
        this.title = doc.getTitle();
        this.content = doc.getContent();
        this.ownerUsername = doc.getOwner().getUsername();
        this.currentUserRole = null; //dodany null jako rola
        this.createdAt = doc.getCreatedAt();
        this.updatedAt = doc.getUpdatedAt();
    }

}
