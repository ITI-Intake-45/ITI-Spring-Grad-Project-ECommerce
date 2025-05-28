package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
  
    //boolean existsByEmail(@NonNull @Email(regexp = ".+@.+\\..+") String email);

    /* Find a user by email address */
    Optional<User>  findByEmail(String email);

    /* Check if a user with the given email exists */
    boolean existsByEmail(String email);

    boolean existsByUserId(int userId);


    /* Change user's password */
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.userId = :userId")
    int updateUserPassword(@Param("userId") int userId, @Param("newPassword") String newPassword);


    /* Reset user's password by email (for password recovery) */
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.email = :email")
    int resetPasswordByEmail(@Param("email") String email, @Param("newPassword") String newPassword);


    /* Update user's credit balance */
    @Modifying
    @Query("UPDATE User u SET u.creditBalance = :newBalance WHERE u.userId = :userId")
    int updateCreditBalance(@Param("userId") int userId, @Param("newBalance") double newBalance);


    /* Subtract amount from user's credit balance */
    @Modifying
    @Query("UPDATE User u SET u.creditBalance = u.creditBalance - :amount WHERE u.userId = :userId AND u.creditBalance >= :amount")
    int subtractFromBalance(@Param("userId") int userId, @Param("amount") double amount);
}

