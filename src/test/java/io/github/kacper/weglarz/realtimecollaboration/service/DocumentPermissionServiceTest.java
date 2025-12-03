package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentPermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentPermissionServiceTest {

    @Mock
    private DocumentPermissionRepository documentPermissionRepository;

    @InjectMocks
    private DocumentPermissionService documentPermissionService;

    @Test
    void should_CreateNewPermission_ForOwner() {
        Document doc = new Document();
        User user = new User();

        documentPermissionService.newPermissionForOwner(doc, user, Role.OWNER);

        verify(documentPermissionRepository).save(any(DocumentPermission.class));
    }

    @Test
    void should_CreateNewPermission_ForShare() {

        Document doc = new Document();
        User user = new User();

        documentPermissionService.newPermissionForShare(doc, user, Role.VIEWER);

        verify(documentPermissionRepository).save(any(DocumentPermission.class));
    }

    @Test
    void should_ReturnFalse_When_IsOwner_And_RoleIsNotOwner() {

        DocumentPermission permission = new DocumentPermission();
        permission.setRole(Role.EDITOR);

        when(documentPermissionRepository.findByUserIdAndDocumentId(1L, 1L))
                .thenReturn(Optional.of(permission));

        boolean result = documentPermissionService.isOwner(1L, 1L);

        assertFalse(result);
    }

    @Test
    void should_ReturnTrue_When_CanEdit_And_RoleIsEditor() {
        DocumentPermission permission = new DocumentPermission();
        permission.setRole(Role.EDITOR);

        when(documentPermissionRepository.findByUserIdAndDocumentId(1L, 1L))
                .thenReturn(Optional.of(permission));

        boolean result = documentPermissionService.canEdit(1L, 1L);

        assertTrue(result);
    }

    @Test
    void should_ReturnFalse_When_CanEdit_And_RoleIsViewer() {
        DocumentPermission permission = new DocumentPermission();
        permission.setRole(Role.VIEWER);

        when(documentPermissionRepository.findByUserIdAndDocumentId(1L, 1L))
                .thenReturn(Optional.of(permission));

        boolean result = documentPermissionService.canEdit(1L, 1L);

        assertFalse(result);
    }
}