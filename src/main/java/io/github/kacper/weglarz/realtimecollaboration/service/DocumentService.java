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

    /**
     * Tworzy nowy dokument
     * @param request tytuł i zawartosc nowego dokumnetu
     * @param owner kto chce stowrzyc nowy dokument
     * @return  odpwoiedz DocumentResponseDTO jako nowo utworzony dokument
     */
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

    /**
     * Szuka dokumentu po tytule
     * @param title tytuł dokumnetu
     * @return  dokument
     */
    public Optional<Document> findByTitle(String title) {
        return documentRepository.findByTitle(title);
    }


    /**
     * Szuka dokumnetu po id
     * @param id dokumnetu
     * @return dokument
     */
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    /**
     * Szuka wskazanego dokumentu
     * @param document wskazany dokuemnt
     * @return dokuemnt
     */
    public DocumentResponseDTO getDocument(Document document) {

        return new DocumentResponseDTO(
                document.getId(),
                document.getTitle(),
                document.getContent(),
                document.getOwner().getUsername(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    /**
     * Pobiera liste dokumnetow usera
     * @param owner user
     * @return liste dokumnentow usera w odpowiedzi DocumentResponseDTO
     */
    public List<DocumentResponseDTO> getDocumentsByOwner(User owner) {
        List<Document> documents = documentRepository.findByOwner(owner);
        return documents.stream()//zamienia liste na strumien
                .map(DocumentResponseDTO::new) //dla kazdego documentu w strumieniu tworzy DocumentResponseDTO
                .toList(); // zwraca response jak liste
    }

    /**
     * Updatuje dokument
     * @param request zmiany do wprowadzania
     * @param document stary doukument
     * @return  odpowiedz DocumentResponseDTO ze zmianami
     */
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

    /**
     * Usuwa dokument
     * @param id dokumentu
     */
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
