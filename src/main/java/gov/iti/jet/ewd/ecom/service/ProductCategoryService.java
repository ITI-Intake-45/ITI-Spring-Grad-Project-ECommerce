package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.entity.ProductCategory;
import java.util.List;
import java.util.Optional;

public interface ProductCategoryService {

    Optional<ProductCategory> getProductCategoryByName(String name);
    List<ProductCategory> getAllProductCategories();
    ProductCategory createProductCategory(String name);
    ProductCategory updateProductCategory(ProductCategory productCategory);
    boolean productCategoryExist(String name);
    boolean removeProductCategoryByName(String name);
    ProductCategory getProductCategoryById(int id);


}
