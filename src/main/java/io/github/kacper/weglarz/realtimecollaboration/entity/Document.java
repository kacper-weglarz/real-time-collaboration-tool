package io.github.kacper.weglarz.realtimecollaboration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
    @NotBlank
    @JoinColumn(name = "owner_Id")
    @ManyToOne
    private User user;


    /**
     * Shared
     */

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
