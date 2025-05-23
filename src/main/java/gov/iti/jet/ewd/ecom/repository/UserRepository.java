package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.User;
import jakarta.validation.constraints.Email;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(@NonNull @Email(regexp = ".+@.+\\..+") String email);

    Optional<User> findByEmail(String email);
}
