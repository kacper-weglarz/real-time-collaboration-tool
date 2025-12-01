package io.github.kacper.weglarz.realtimecollaboration.repository;

import io.github.kacper.weglarz.realtimecollaboration.entity.DocumentPermission;
import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentPermissionRepository extends JpaRepository<DocumentPermission, Long> {

    Optional<DocumentPermission> findByUserIdAndDocumentId(Long userId, Long documentId);

    List<DocumentPermission> findByUserId(Long userId);
}
