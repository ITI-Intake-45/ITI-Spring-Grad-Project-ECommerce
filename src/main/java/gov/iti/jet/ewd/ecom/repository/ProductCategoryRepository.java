package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    Optional<ProductCategory> findByName(String name);

    boolean existsById(int id);

    boolean existsByName(String name);

    void deleteByName(String name);
}
