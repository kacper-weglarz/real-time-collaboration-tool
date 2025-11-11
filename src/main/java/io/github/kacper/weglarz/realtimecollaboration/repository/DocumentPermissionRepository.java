package io.github.kacper.weglarz.realtimecollaboration.repository;

import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Document Permission Repository
 */
@Repository
public interface DocumentPermissionRepository extends JpaRepository<DocumentPermission, Long> {

    /**
     * Looking for permision for this user and this document
     * @param userId id current user
     * @param documentId id of document
     */
    Optional<DocumentPermission> findByUserIdAndDocumentId(Long userId, Long documentId);

    List<DocumentPermission> findByUserId(Long userId);
}
