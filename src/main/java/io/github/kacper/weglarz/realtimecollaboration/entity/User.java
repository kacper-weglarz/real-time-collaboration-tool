package io.github.kacper.weglarz.realtimecollaboration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

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
    @NotNull
    private String username;

    /**
     * User email
     */
    @Column(unique = true, length = 100)
    @NotNull
    private String email;

    /**
     * Hashed password
     */
    @Column(length = 250)
    @NotNull
    private String passwordHash;

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
