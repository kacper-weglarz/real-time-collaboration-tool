package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.ShareDocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.DocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.ShareDocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.DocumentNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UnauthorizedAccessException;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UserNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentRepository;
import io.github.kacper.weglarz.realtimecollaboration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentPermissionService documentPermissionService;
    private final UserService userService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, DocumentPermissionService documentPermissionService,
                           UserService userService) {
        this.documentRepository = documentRepository;
        this.documentPermissionService = documentPermissionService;
        this.userService = userService;
    }

    /**
     * Creates new document
     * @param request DTO -> title, content
     * @param owner who created
     * @return DocumentResponseDTO OK with role - OWNER
     */
    public DocumentResponseDTO createDocument(DocumentRequestDTO request, User owner) {
        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setOwner(owner);
        Document savedDocument = documentRepository.save(document);

        documentPermissionService.newPermissionForOwner(savedDocument, owner, Role.OWNER); // creates new DocumentPermission for OWNER

        return new DocumentResponseDTO(
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getContent(),
                owner.getUsername(),
                Role.OWNER,
                savedDocument.getCreatedAt(),
                savedDocument.getUpdatedAt()
        );
    }

    /**
     * Finds a document by id
     * @param id of document
     * @return document
     */
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    /**
     * Returns document data for a user if they have access
     * @param document target document
     * @param userId current user id
     * @return DocumentResponseDTO OK
     */
    public DocumentResponseDTO getDocument(Document document, Long userId) {

        if (!documentPermissionService.hasAccess(userId, document.getId())) {
            throw new UnauthorizedAccessException("Not allowed to access this document");
        }

        Role userRole = documentPermissionService.getUserRole(userId,document.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("Permission not found"));

        return new DocumentResponseDTO(
                document.getId(),
                document.getTitle(),
                document.getContent(),
                document.getOwner().getUsername(),
                userRole,
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    /**
     * Gets List of user documents
     * @param userId id user
     * @return DocumentResponseDTO as a List
     */
    public List<DocumentResponseDTO> getUsersDocuments(Long userId) {

        List<DocumentPermission> permissions = documentPermissionService.findByUserId(userId);

        return permissions.stream()
                .map(p -> {
                    Document doc =  p.getDocument();
                    return new DocumentResponseDTO(
                            doc.getId(),
                            doc.getTitle(),
                            doc.getContent(),
                            doc.getOwner().getUsername(),
                            p.getRole(),
                            doc.getCreatedAt(),
                            doc.getUpdatedAt()
                    );
                })
                .toList();
    }

    /**
     * Updates the document if the user has EDIT or OWNER permissions
     * @param request DTO -> edited fields and EDITOR/OWNER information
     * @param document existing document
     * @param userId current user ID
     * @return updated DocumentResponseDTO
     */
    public DocumentResponseDTO updateDocument(DocumentRequestDTO request, Document document, Long userId) {

        if (!documentPermissionService.canEdit(userId, document.getId())) {
            throw new UnauthorizedAccessException("User is not allowed to edit this document");
        }

        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        Document savedDocument = documentRepository.save(document);

        Role role = documentPermissionService.getUserRole(userId, document.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("User is not allowed to edit this document"));

        return new DocumentResponseDTO(
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getContent(),
                document.getOwner().getUsername(),
                role,
                savedDocument.getCreatedAt(),
                savedDocument.getUpdatedAt()
        );
    }

    /**
     * Shares a document with another user and assigns a role
     * @param request target username and role
     * @param docId of document to share
     * @param currentUserId of user sharing the document
     * @return ShareDocumentResponseDTO
     */
    public ShareDocumentResponseDTO shareDocument(ShareDocumentRequestDTO request, Long docId, Long currentUserId) {

        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found" + request.getUsername()));

        if (documentPermissionService.hasAccess(user.getId(), docId)) {
            throw new UnauthorizedAccessException("User already has access to this document");
        }

        Role roleToGive;

        if (documentPermissionService.isOwner(currentUserId, docId)) {
            roleToGive = request.getRole();
        } else if (documentPermissionService.hasAccess(currentUserId, docId)) {
            roleToGive = Role.VIEWER;
        } else {
            throw new UnauthorizedAccessException("User is not allowed to share this document");
        }

        documentPermissionService.newPermissionForShare(doc, user, roleToGive);

        return new ShareDocumentResponseDTO(
                doc.getId(),
                roleToGive,
                user.getId()
        );
    }

    /**
     * Deletes document
     * @param id of document
     */
    public void deleteDocument(Long id, Long userid) {
        if (!documentPermissionService.isOwner(userid, id)) {
            throw new UnauthorizedAccessException("User has no permission to delete document");
        }
        documentRepository.deleteById(id);
    }
}
