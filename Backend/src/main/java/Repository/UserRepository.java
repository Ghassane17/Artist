// src/main/java/com/artapp/backend/user/UserRepository.java
package Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}