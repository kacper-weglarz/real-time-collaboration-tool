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

        Optional<Document> findByTitle(String title);

        List<Document> findByOwner(User owner);


}
