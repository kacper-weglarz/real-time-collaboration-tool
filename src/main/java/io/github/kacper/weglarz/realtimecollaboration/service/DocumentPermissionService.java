package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
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
     * Szuka roli usera
     * @param userId id usera
     * @param documentId id dokumentu
     * @return zwraca role usera do konkretnego dokumentu
     */
    public Optional<Role> getUserRole(Long userId, Long documentId) {

        return documentPermissionRepository.findByUserIdAndDocumentId(userId, documentId)
                .map(DocumentPermission::getRole);
    }

    /**
     *
     * @param userId
     * @return
     */
    public List<DocumentPermission> findByUserId(Long userId) {
        return documentPermissionRepository.findByUserId(userId);
    }

    /**
     * Creates new Document Permission for owner of doc
     * @param document
     * @param user
     * @param role OWNER
     * @return object documentPermission
     */
    public DocumentPermission newPermissionForOwner(Document document, User user, Role role) {

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
                .orElse(false);

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
                .orElse(false);
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
                .orElse(false);
    }

}
