package io.github.kacper.weglarz.realtimecollaboration.controller;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.DocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
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

    @PostMapping
    public ResponseEntity<DocumentResponseDTO> createDocument(Authentication authentication,
                                                              @RequestBody DocumentRequestDTO documentRequestDTO) {
        String username = authentication.getName();
        User owner = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DocumentResponseDTO response = documentService.createDocument(documentRequestDTO, owner);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<DocumentResponseDTO> getMyDocuments(Authentication authentication) {

        String username = authentication.getName();
        User owner =  userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return documentService.getDocumentsByOwner(owner);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(Authentication authentication, @PathVariable Long id,
                                   @RequestBody DocumentRequestDTO documentRequestDTO) {

        String username = authentication.getName();

        Document existingDocument = documentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!existingDocument.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }

        DocumentResponseDTO response = documentService.updateDocument(documentRequestDTO, existingDocument);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();

        Document document = documentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!document.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }

        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
