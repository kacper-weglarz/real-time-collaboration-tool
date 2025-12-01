package io.github.kacper.weglarz.realtimecollaboration.controller;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.request.ShareDocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.DocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.ShareDocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.DocumentNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UserNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.service.DocumentService;
import io.github.kacper.weglarz.realtimecollaboration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    @Autowired
    public DocumentController(DocumentService documentService,  UserService userService) {
        this.documentService = documentService;
        this.userService = userService;
    }

    /**
     * Creates new document
     * @param authentication Logged-in users data
     * @param documentRequestDTO DTO -> title and content
     * @return DocumentResponseDTO OK
     */
    @PostMapping
    public ResponseEntity<DocumentResponseDTO> createDocument(Authentication authentication,
                                                              @RequestBody DocumentRequestDTO documentRequestDTO) {
        String username = authentication.getName();
        User owner = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + username));

        DocumentResponseDTO response = documentService.createDocument(documentRequestDTO, owner);

        return ResponseEntity.ok(response);
    }

    /**
     * Shares document
     * @param authentication Logged-in users data
     * @param id of document for sharing
     * @param shareDocumentRequestDTO DTO -> role, username
     * @return ShareDocumentResponseDTO OK
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<ShareDocumentResponseDTO> shareDocument(Authentication authentication, @PathVariable Long id,
                                                                  @RequestBody ShareDocumentRequestDTO shareDocumentRequestDTO) {
        String username = authentication.getName();

        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + username));

        ShareDocumentResponseDTO response = documentService.shareDocument(shareDocumentRequestDTO, id, currentUser.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Gets all user documents
     * @param authentication Logged-in users data
     * @return DocumentResponseDTO as List OK
     */
    @GetMapping
    public List<DocumentResponseDTO> getMyDocuments(Authentication authentication) {

        String username = authentication.getName();

        User owner =  userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + username));

        return documentService.getUsersDocuments(owner.getId());
    }

    /**
     * Gets choosen user document
     * @param authentication Logged-in users data
     * @param id of document
     * @return DocumentREsponseDTO OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getDocument(Authentication authentication, @PathVariable Long id) {

        String username = authentication.getName();

        Document existingDocument = documentService.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));

        User currentUser = userService.findByUsername(username)
                .orElseThrow(() ->  new UserNotFoundException("User not found " + username));

        DocumentResponseDTO response = documentService.getDocument(existingDocument, currentUser.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Editing document
     * @param authentication Logged-in users data
     * @param id of document to edit
     * @param documentRequestDTO DTO -> title, content
     * @return DocumentResponseDTO OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(Authentication authentication, @PathVariable Long id,
                                   @RequestBody DocumentRequestDTO documentRequestDTO) {

        String username = authentication.getName();

        Document existingDocument = documentService.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));

        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + username));

        DocumentResponseDTO response = documentService.updateDocument(documentRequestDTO, existingDocument, currentUser.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes document
     * @param authentication Logged-in users data
     * @param id of document to delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();

        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + username));

        documentService.deleteDocument(id, currentUser.getId());

        return ResponseEntity.noContent().build();
    }
}
