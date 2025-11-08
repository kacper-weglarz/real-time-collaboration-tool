package io.github.kacper.weglarz.realtimecollaboration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Users entity
 */
@NoArgsConstructor @Data
@Entity @AllArgsConstructor
@Table(name = "users")
public class User {

    /**
     * Primary key -> id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username
     */
    @Column(unique = true, length = 50)
    @NotBlank(message = "Username cannot be empty")
    private String username;

    /**
     * User email
     */
    @Column(unique = true, length = 100)
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /**
     * Hashed password
     */
    @Column(length = 250)
    @NotBlank(message = "Password cannot be empty")
    private String passwordHash;

    /**
     * Lista dokumentów usera
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    /**
     * When user was created
     */
    @Column @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * When user was updated
     */
    @Column @UpdateTimestamp
    private LocalDateTime updatedAt;

}
