package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.DocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public DocumentResponseDTO createDocument(DocumentRequestDTO request, User owner) {
        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setOwner(owner);
        Document savedDocument = documentRepository.save(document);

        return new DocumentResponseDTO(
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getContent(),
                owner.getUsername(),
                savedDocument.getCreatedAt(),
                savedDocument.getUpdatedAt()
        );
    }

    public Optional<Document> findByTitle(String title) {
        return documentRepository.findByTitle(title);
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    public List<DocumentResponseDTO> getDocumentsByOwner(User owner) {
        List<Document> documents = documentRepository.findByOwner(owner);
        return documents.stream()
                .map(DocumentResponseDTO::new)
                .toList();
    }

    public DocumentResponseDTO updateDocument(DocumentRequestDTO request, Document document) {

        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        Document savedDocument = documentRepository.save(document);

        return new DocumentResponseDTO(
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getContent(),
                document.getOwner().getUsername(),
                savedDocument.getCreatedAt(),
                savedDocument.getUpdatedAt()
        );
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
