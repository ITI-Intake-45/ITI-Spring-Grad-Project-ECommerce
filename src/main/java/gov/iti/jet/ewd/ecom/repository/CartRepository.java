package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // kerellos: here we could remove this custom method and only use the findById [the inherited method from JpaRepository] because it provides the Optional wrapper for better null handling but ok
    Cart findByCartId(int userId);
}
