package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    Optional<ProductCategory> findByName(String name);
    boolean existsByName(String name);
    @Modifying
    int deleteByName(String name);

//    @Modifying
//    @Query("Update ProductCategory c SET c.name = :name WHERE c.id = :id")
//    int updateProductCategory(@Param("id") int id, @Param("name") String name);


}
