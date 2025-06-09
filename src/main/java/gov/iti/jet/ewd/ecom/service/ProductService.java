package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.entity.Product;
import jakarta.mail.Multipart;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product, MultipartFile imageFile);
    void updateProduct(Product product, MultipartFile imageFile);
    void deleteProductById(int id);
    Product getProductById(int id);
    List<Product> getAllProducts();
    List<Product> getProductsByCategoryId(int categoryId);
    boolean productExists(String name);
    public Page<Product> filterProducts(String name, String category, Double minPrice, Double maxPrice, String sortDir, int page, int size);
}
