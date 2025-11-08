package io.github.kacper.weglarz.realtimecollaboration.repository;

import io.github.kacper.weglarz.realtimecollaboration.entity.Document;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Document Repository
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

        /**
         * Szuka dokumnetu przez tytuł
         * @param title poszukiawny tytuł
         * @return zwraca dokument jeśli istnieje
         */
        Optional<Document> findByTitle(String title);

        /**
         * Szuka dokumnetow usera
         * @param owner poszukiwany user
         * @return zwraca liste dokumentow usera
         */
        List<Document> findByOwner(User owner);

}
