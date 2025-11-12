package io.github.kacper.weglarz.realtimecollaboration.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Documents entity
 */
@NoArgsConstructor @Data
@Entity @AllArgsConstructor
@Table(name = "documents")
public class Document {

    /**
     * Primary key -> id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Document title
     */
    @Column(length = 200)
    private String title;

    /**
     * Document content
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Owner of document
     */
    @JsonIgnore
    @JoinColumn(name = "owner_id")
    @ManyToOne(optional = false)
    private User owner;


    /**
     * List of permissions for document !!!!!!!!!!!!!!!!
     */
    @JsonIgnore
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentPermission> documentPermissions = new ArrayList<>();

    /**
     * When document was created
     */
    @Column @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * When document was updated
     */
    @Column @UpdateTimestamp
    private LocalDateTime updatedAt;
}
