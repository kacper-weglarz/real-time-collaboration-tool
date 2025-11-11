package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentPermissionService {


    private final DocumentPermissionRepository documentPermissionRepository;

    @Autowired
    public DocumentPermissionService(DocumentPermissionRepository documentPermissionRepository) {
        this.documentPermissionRepository = documentPermissionRepository;
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
     * Checks if user is editor
     * @param userId id current user
     * @param documentId id of document
     * @return true if user is editor
     */
    public boolean isEditor(Long userId, Long documentId) {
        return documentPermissionRepository.findByUserIdAndDocumentId(userId, documentId)
                .map(p -> p.getRole() == Role.EDITOR)
                .orElse(false);
    }

    /**
     * Checks if user is viewer
     * @param userId id current user
     * @param documentId id of document
     * @return true if user is viewer
     */
    public boolean isViewer(Long userId, Long documentId) {
        return documentPermissionRepository.findByUserIdAndDocumentId(userId, documentId)
                .map(p -> p.getRole() == Role.VIEWER)
                .orElse(false);
    }


}
