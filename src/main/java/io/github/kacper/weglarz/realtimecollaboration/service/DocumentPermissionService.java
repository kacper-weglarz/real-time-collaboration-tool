package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UnauthorizedAccessException;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentPermissionService {


    private final DocumentPermissionRepository documentPermissionRepository;

    @Autowired
    public DocumentPermissionService(DocumentPermissionRepository documentPermissionRepository) {
        this.documentPermissionRepository = documentPermissionRepository;
    }

    /**
     * Looks for user role
     * @param userId current user id
     * @param documentId id of this document
     * @return users role for this document
     */
    public Optional<Role> getUserRole(Long userId, Long documentId) {

        return documentPermissionRepository.findByUserIdAndDocumentId(userId, documentId)
                .map(DocumentPermission::getRole);
    }

    public List<DocumentPermission> findByUserId(Long userId) {
        return documentPermissionRepository.findByUserId(userId);
    }

    /**
     * Creates a new permission for the document owner
     * @param document document entity
     * @param user user entity
     * @param role -> OWNER
     * @return created DocumentPermission
     */
    public DocumentPermission newPermissionForOwner(Document document, User user, Role role) {

        DocumentPermission docPermission = new DocumentPermission();

        docPermission.setDocument(document);
        docPermission.setUser(user);
        docPermission.setRole(role);

        return documentPermissionRepository.save(docPermission);
    }

    /**
     * Creates a new permission when a document is shared
     * @param document document entity
     * @param user user entity
     * @param role -> VIEWER/EDITOR)
     * @return created DocumentPermission
     */
    public DocumentPermission newPermissionForShare(Document document, User user, Role role) {
        DocumentPermission docPermission = new DocumentPermission();

        docPermission.setDocument(document);
        docPermission.setUser(user);
        docPermission.setRole(role);

        return documentPermissionRepository.save(docPermission);
    }

    /**
     * Checks if user is owner
     * @param userId id current user
     * @param documentId id of document
     * @return true if user is owner
     */
    public boolean isOwner(Long userId, Long documentId) {
        return documentPermissionRepository.findByUserIdAndDocumentId(userId, documentId)
                .map(p -> p.getRole() == Role.OWNER)
                .orElseThrow(() -> new UnauthorizedAccessException("Permission not found"));

    }

    /**
     * Checks if user can edit
     * @param userId id current user
     * @param documentId id of document
     * @return true if user can edit
     */
    public boolean canEdit(Long userId, Long documentId) {
        return documentPermissionRepository.findByUserIdAndDocumentId(userId, documentId)
                .map(p -> p.getRole() == Role.EDITOR || p.getRole() == Role.OWNER)
                .orElseThrow(() -> new UnauthorizedAccessException("Permission not found"));
    }

    /**
     * Checks if user has access
     * @param userId id current user
     * @param documentId id of document
     * @return true if user has access
     */
    public boolean hasAccess(Long userId, Long documentId) {
        return documentPermissionRepository.findByUserIdAndDocumentId(userId, documentId)
                .map(p -> p.getRole() == Role.OWNER ||
                                            p.getRole() == Role.EDITOR ||
                                            p.getRole() == Role.VIEWER)
                .orElseThrow(() -> new UnauthorizedAccessException("Permission not found"));
    }

}
