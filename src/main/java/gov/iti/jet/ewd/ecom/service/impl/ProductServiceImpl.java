package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.entity.Product;
import gov.iti.jet.ewd.ecom.exception.ProductNotFoundException;
import gov.iti.jet.ewd.ecom.repository.ProductRepository;
import gov.iti.jet.ewd.ecom.service.ProductService;
import gov.iti.jet.ewd.ecom.util.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        Product existing = productRepository.findById(product.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + product.getProductId()));
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());
        existing.setCategory(product.getCategory());

        return productRepository.save(existing);
    }

    @Override
    public boolean deleteProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        product.setPrice(0.0);
        product.setStock(0);
        productRepository.save(product); // Update with new values

        return true;
    }


    @Override
    public Product getProductById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategoryId(int categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public boolean productExists(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public Page<Product> filterProducts(String name, String category, Double minPrice, Double maxPrice, String sortDir, int page, int size) {
    Specification<Product> spec = Specification
            .where(ProductSpecification.hasName(name))
            .and(ProductSpecification.hasCategory(category))
            .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice));

    Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by("price").descending() :
            Sort.by("price").ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    return productRepository.findAll(spec, pageable);
}
}
