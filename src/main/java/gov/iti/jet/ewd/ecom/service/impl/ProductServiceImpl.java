package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.entity.OrderItem;
import gov.iti.jet.ewd.ecom.entity.Product;
import gov.iti.jet.ewd.ecom.exception.ProductNotFoundException;
import gov.iti.jet.ewd.ecom.repository.OrderItemRepository;
import gov.iti.jet.ewd.ecom.repository.ProductRepository;
import gov.iti.jet.ewd.ecom.service.FileStorageService;
import gov.iti.jet.ewd.ecom.service.ProductService;
import gov.iti.jet.ewd.ecom.util.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              OrderItemRepository orderItemRepository,
                              FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Product createProduct(Product product, MultipartFile imageFile) {
        String imagePath = fileStorageService.storeFile(imageFile);
        product.setImage(imagePath);

        return productRepository.save(product);
    }

    @Override
    public void updateProduct(Product product, MultipartFile imageFile) {
        Product existing = productRepository.findById(product.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + product.getProductId()));

        if (imageFile != null) {
            String imagePath = fileStorageService.storeFile(imageFile);
            product.setImage(imagePath);
        } else {
            product.setImage(existing.getImage());
        }

        productRepository.save(product);
    }

    @Override
    public void deleteProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        List<OrderItem> items = orderItemRepository.findAllByProduct(product);
        if (!items.isEmpty()) {
            throw new RuntimeException("Cannot delete product with ID: " + id + " as it is in use by " + items.size() + " order items");
        }

        productRepository.delete(product);
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
