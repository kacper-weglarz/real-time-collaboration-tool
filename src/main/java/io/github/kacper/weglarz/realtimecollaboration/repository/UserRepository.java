package io.github.kacper.weglarz.realtimecollaboration.repository;

import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Search for a user by a unique username
     * @param username searched username
     * @return User object if exist
     */
    Optional<User> findByUsername(String username);

    /**
     * Search for a user by a unique email
     * @param email searched email
     * @return User object exist
     */
    Optional<User> findByEmail(String email);
}
