package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.Product;
import gov.iti.jet.ewd.ecom.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>  , JpaSpecificationExecutor<Product> {
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findByCategory(ProductCategory category);
    boolean existsByName(String name);
}
