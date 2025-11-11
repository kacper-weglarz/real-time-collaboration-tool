package io.github.kacper.weglarz.realtimecollaboration.service;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import io.github.kacper.weglarz.realtimecollaboration.dto.response.DocumentResponseDTO;
import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.Role;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentPermissionService documentPermissionService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, DocumentPermissionService documentPermissionService) {
        this.documentRepository = documentRepository;
        this.documentPermissionService = documentPermissionService;
    }

    /**
     * Tworzy nowy dokument
     * @param request tytuł i zawartosc nowego dokumnetu
     * @param owner kto chce stowrzyc nowy dokument
     * @return  odpwoiedz DocumentResponseDTO jako nowo utworzony dokument z rola  OWNER
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
    public DocumentResponseDTO getDocument(Document document, Long userId) {

        if (!documentPermissionService.hasAccess(userId, document.getId())) {
            throw new RuntimeException("Not allowed to access this document");
        }

        Role userRole = documentPermissionService.getUserRole(userId,document.getId())
                .orElseThrow(() -> new RuntimeException("User is not allowed to access this document"));

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
     * Pobiera liste dokumnetow usera
     * @param owner user
     * @return liste dokumnentow usera w odpowiedzi DocumentResponseDTO
     */
    @Deprecated
    public List<DocumentResponseDTO> getDocumentsByOwner(User owner) {
        List<Document> documents = documentRepository.findByOwner(owner);
        return documents.stream()//zamienia liste na strumien
                .map(DocumentResponseDTO::new) //dla kazdego documentu w strumieniu tworzy DocumentResponseDTO
                .toList(); // zwraca response jak liste
    }

    /**
     * Pobiera liste dokumnetow usera
     * @param userId id usera
     * @return liste dokumnentow usera w odpowiedzi DocumentResponseDTO z ROLA
     */
    public List<DocumentResponseDTO> getUsersDocuments(Long userId) {

        List<DocumentPermission> permissions = documentPermissionService.findByUserId(userId); // pobiera liste uprawnien usera

        return permissions.stream() // na kolekcje
                .map(p -> { //dla kazdego permission
                    Document doc =  p.getDocument(); // pobierz dokument
                    return new DocumentResponseDTO( //stworz responseDTO
                            doc.getId(),
                            doc.getTitle(),
                            doc.getContent(),
                            doc.getOwner().getUsername(),
                            p.getRole(),
                            doc.getCreatedAt(),
                            doc.getUpdatedAt()
                    );
                })
                .toList(); //na liste
    }

    /**
     * Updatuje dokument
     * @param request zmiany do wprowadzania
     * @param document stary doukument
     * @return  odpowiedz DocumentResponseDTO ze zmianami
     */
    public DocumentResponseDTO updateDocument(DocumentRequestDTO request, Document document, Long userId) {

        if (!documentPermissionService.canEdit(userId, document.getId())) {
            throw new RuntimeException("User is not allowed to edit this document");
        }

        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        Document savedDocument = documentRepository.save(document);

        Role role = documentPermissionService.getUserRole(userId, document.getId())
                .orElseThrow(() -> new RuntimeException("User is not allowed to edit this document"));

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
     * Usuwa dokument
     * @param id dokumentu
     */
    public void deleteDocument(Long id, Long userid) {
        if (!documentPermissionService.isOwner(userid, id)) {
            throw new RuntimeException("User has no permission to delete document");
        }
        documentRepository.deleteById(id);
    }
}
