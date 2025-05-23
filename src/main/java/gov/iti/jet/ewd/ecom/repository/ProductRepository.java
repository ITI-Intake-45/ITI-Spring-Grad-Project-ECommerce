package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
