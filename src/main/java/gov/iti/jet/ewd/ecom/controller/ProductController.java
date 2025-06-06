package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.entity.Product;
import gov.iti.jet.ewd.ecom.service.FileStorageService;
import gov.iti.jet.ewd.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @Autowired
    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

    @PostMapping
//    @PreAuthorize("hasRole={'ROLE_ADMIN'}") // to secure and endpoint with role or permission
//    @PreAuthorize("hasAuthority={'add-product'}") // to secure and endpoint with role or permission
    public ResponseEntity<Product> createProduct(@RequestBody Product product, @RequestPart("image") MultipartFile image) {
        String imagePath = fileStorageService.storeFile(image);
        product.setImage(imagePath);
        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product product,  @RequestPart("image") MultipartFile image) {
        product.setProductId(id);
        String imagePath = fileStorageService.storeFile(image);
        product.setImage(imagePath);
        return productService.updateProduct(product);
    }

    @DeleteMapping("/{id}")
    public boolean deleteProduct(@PathVariable int id) {
        return productService.deleteProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable int categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    //http://localhost:8080/api/v1/products/filter?minPrice=0&maxPrice=200&sortDir=desc&page=1&size=3
    @GetMapping("/filter")
    public ResponseEntity<Page<Product>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Product> result = productService.filterProducts(name, category, minPrice, maxPrice, sortDir, page, size);
        return ResponseEntity.ok(result);
    }

}
