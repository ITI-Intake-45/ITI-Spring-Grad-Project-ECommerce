package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface CartRepository extends JpaRepository<Cart, Integer> {
}
