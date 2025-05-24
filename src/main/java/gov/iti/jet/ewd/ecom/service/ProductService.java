package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(Product product);
    boolean deleteProductById(int id);
    Product getProductById(int id);
    List<Product> getAllProducts();
    List<Product> getProductsByCategoryId(int categoryId);
    boolean productExists(String name);
    public Page<Product> filterProducts(String name, String category, Double minPrice, Double maxPrice, String sortDir, int page, int size);
}
