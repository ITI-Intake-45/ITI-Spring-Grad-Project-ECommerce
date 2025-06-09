package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    Optional<ProductCategory> findByName(String name);

    boolean existsById(int id);

    boolean existsByName(String name);

    @Modifying
    int deleteByName(String name);

    @Modifying
    int deleteById(int id);

}
