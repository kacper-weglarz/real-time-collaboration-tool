package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.DocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentPermissionService documentPermissionService;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void should_CreateDocument_And_Set_OwnerPermission() {

        DocumentRequestDTO request = new DocumentRequestDTO();
        request.setTitle("Title");
        request.setContent("Content");

        User owner =  new User();
        owner.setUsername("Owner");

        Document savedDocument = new Document();
        savedDocument.setId(1L);
        savedDocument.setTitle(request.getTitle());
        savedDocument.setContent(request.getContent());
        savedDocument.setOwner(owner);
        savedDocument.setCreatedAt(LocalDateTime.now());
        savedDocument.setUpdatedAt(LocalDateTime.now());

        when(documentRepository.save(any(Document.class))).thenReturn(savedDocument);

        DocumentResponseDTO response = documentService.createDocument(request,owner);

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Content", response.getContent());
        assertEquals("Owner", response.getOwnerUsername());
        assertEquals(Role.OWNER,response.getCurrentUserRole());
        assertEquals(savedDocument.getCreatedAt(), response.getCreatedAt());
        assertEquals(savedDocument.getUpdatedAt(), response.getUpdatedAt());
        Mockito.verify(documentRepository).save(any(Document.class));
        Mockito.verify(documentPermissionService).newPermissionForOwner(eq(savedDocument), eq(owner), eq(Role.OWNER));
    }
}
