package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.ShareDocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.DocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.ShareDocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.DocumentNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UnauthorizedAccessException;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UserNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentPermissionService documentPermissionService;

    @Mock
    private UserService userService;

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

        verify(documentRepository).save(any(Document.class));
    }


    @Test
    void should_getDocument() {

        User owner = new User();
        owner.setId(1L);
        owner.setUsername("Owner");

        Document document = new Document();
        document.setId(1L);
        document.setTitle("Title");
        document.setContent("Content");
        document.setOwner(owner);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        when(documentPermissionService.hasAccess(1L,1L)).thenReturn(true);
        when(documentPermissionService.getUserRole(1L,1L)).thenReturn(Optional.of(Role.OWNER));

        DocumentResponseDTO response = documentService.getDocument(document,owner.getId());

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Content", response.getContent());
        assertEquals("Owner", response.getOwnerUsername());
        assertEquals(Role.OWNER, response.getCurrentUserRole());
        assertEquals(document.getCreatedAt(), response.getCreatedAt());
        assertEquals(document.getUpdatedAt(), response.getUpdatedAt());
    }

    @Test
    void should_Throw_UnauthorizedAccessException_When_UserIsNotAllowed_ToAccessDocument() {

        User owner = new User();
        owner.setId(1L);

        Document document = new Document();
        document.setId(2L);

        when(documentPermissionService.hasAccess(1L, 2L)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> {
            documentService.getDocument(document, owner.getId());
        });
    }

    @Test
    void should_UpdateDocument() {

        User owner = new User();
        owner.setId(1L);

        Document document = new Document();
        document.setId(1L);
        document.setTitle("Title");
        document.setContent("Content");
        document.setOwner(owner);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        DocumentRequestDTO  request = new DocumentRequestDTO();
        request.setTitle("New Title");
        request.setContent("New Content");

        when(documentPermissionService.canEdit(1L,1L)).thenReturn(true);
        when(documentRepository.save(any(Document.class))).thenReturn(document);
        when(documentPermissionService.getUserRole(1L,1L)).thenReturn(Optional.of(Role.OWNER));

        DocumentResponseDTO response = documentService.updateDocument(request,document, owner.getId());

        assertEquals(1L, response.getId());
        assertEquals("New Title", response.getTitle());
        assertEquals("New Content", response.getContent());
        assertEquals(Role.OWNER, response.getCurrentUserRole());
        assertEquals(document.getCreatedAt(), response.getCreatedAt());
        assertEquals(document.getUpdatedAt(), response.getUpdatedAt());

        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void should_Throw_UnauthorizedAccessException_When_UserIsNotAllowed_ToEditDocument() {

        User owner = new User();
        owner.setId(1L);

        Document document = new Document();
        document.setId(1L);

        DocumentRequestDTO request = new DocumentRequestDTO();
        request.setTitle("New Title");
        request.setContent("New Content");

        when(documentPermissionService.canEdit(1L,1L)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> {
            documentService.updateDocument(request, document, owner.getId());
        });
    }

    @Test
    void should_ShareDocument_WhenOwnerAndUserDoesNotHaveAccess() {

        ShareDocumentRequestDTO request = new ShareDocumentRequestDTO();
        request.setUsername("newUser");
        request.setRole(Role.EDITOR);

        Document doc = new Document();
        doc.setId(1L);

        User user = new User();
        user.setId(2L);
        user.setUsername("newUser");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userService.findByUsername("newUser")).thenReturn(Optional.of(user));
        when(documentPermissionService.hasAccess(2L, 1L)).thenReturn(false);
        when(documentPermissionService.isOwner(1L, 1L)).thenReturn(true);

        ShareDocumentResponseDTO response = documentService.shareDocument(request, 1L, 1L);

        assertEquals(1L, response.getDocumentId());
        assertEquals(Role.EDITOR, response.getSharedWithRole());
        assertEquals(2L, response.getSharedToUserId());
    }

    @Test
    void should_ShareDocument_WhenUserHasAccessButNotOwner() {

        ShareDocumentRequestDTO request = new ShareDocumentRequestDTO();
        request.setUsername("newUser");
        request.setRole(Role.EDITOR);

        Document doc = new Document();
        doc.setId(1L);

        User user = new User();
        user.setId(2L);
        user.setUsername("newUser");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userService.findByUsername("newUser")).thenReturn(Optional.of(user));
        when(documentPermissionService.hasAccess(2L, 1L)).thenReturn(false);
        when(documentPermissionService.isOwner(1L, 1L)).thenReturn(false);
        when(documentPermissionService.hasAccess(1L, 1L)).thenReturn(true);

        ShareDocumentResponseDTO response = documentService.shareDocument(request, 1L, 1L);

        assertEquals(1L, response.getDocumentId());
        assertEquals(Role.VIEWER, response.getSharedWithRole());
        assertEquals(2L, response.getSharedToUserId());
    }

    @Test
    void should_Throw_DocumentNotFoundException_WhenSharedDocument_DoesNotExist() {

        ShareDocumentRequestDTO request = new ShareDocumentRequestDTO();
        request.setUsername("newUser");
        request.setRole(Role.EDITOR);

        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> {
            documentService.shareDocument(request, 1L, 1L);
        });
    }

    @Test
    void should_Throw_UserNotFoundException_WhenSharedDocument_UserDoesNotExist() {

        ShareDocumentRequestDTO request = new ShareDocumentRequestDTO();
        request.setUsername("newUser");
        request.setRole(Role.EDITOR);

        Document doc = new Document();
        doc.setId(1L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userService.findByUsername("newUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            documentService.shareDocument(request, 1L, 1L);
        });
    }

    @Test
    void should_Throw_UnauthorizedAccessException_WhenSharedDocument_UserAlreadyHasAccess() {

        ShareDocumentRequestDTO request = new ShareDocumentRequestDTO();
        request.setUsername("newUser");
        request.setRole(Role.EDITOR);

        Document doc = new Document();
        doc.setId(1L);

        User user = new User();
        user.setId(2L);
        user.setUsername("newUser");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userService.findByUsername("newUser")).thenReturn(Optional.of(user));
        when(documentPermissionService.hasAccess(2L, 1L)).thenReturn(true);

        assertThrows(UnauthorizedAccessException.class, () -> {
            documentService.shareDocument(request, 1L, 1L);
        });
    }

    @Test
    void should_Throw_UnauthorizedAccessException_WhenUserIsNotAllowedToShare() {

        ShareDocumentRequestDTO request = new ShareDocumentRequestDTO();
        request.setUsername("newUser");
        request.setRole(Role.EDITOR);

        Document doc = new Document();
        doc.setId(1L);

        User user = new User();
        user.setId(2L);
        user.setUsername("newUser");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userService.findByUsername("newUser")).thenReturn(Optional.of(user));
        when(documentPermissionService.hasAccess(2L, 1L)).thenReturn(false);
        when(documentPermissionService.isOwner(1L, 1L)).thenReturn(false);
        when(documentPermissionService.hasAccess(1L, 1L)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> {
            documentService.shareDocument(request, 1L, 1L);
        });
    }

    @Test
    void should_DeleteDocument_WhenUserIsOwner() {

        when(documentPermissionService.isOwner(1L, 1L)).thenReturn(true);

        documentService.deleteDocument(1L, 1L);

        verify(documentRepository).deleteById(1L);
    }

    @Test
    void should_Throw_UnauthorizedAccessException_WhenUserIsNotOwner() {

        when(documentPermissionService.isOwner(1L, 1L)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> {
            documentService.deleteDocument(1L, 1L);
        });
    }
}
