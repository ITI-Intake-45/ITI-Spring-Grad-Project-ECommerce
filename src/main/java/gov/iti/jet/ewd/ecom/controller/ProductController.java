package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.entity.Product;
import gov.iti.jet.ewd.ecom.service.ProductCategoryService;
import gov.iti.jet.ewd.ecom.service.ProductService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductController(ProductService productService,
                             ProductCategoryService productCategoryService) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
    }

    @GetMapping
    List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    Product getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

//    @PreAuthorize("hasRole={'ROLE_ADMIN'}") // to secure and endpoint with role or permission
//    @PreAuthorize("hasAuthority={'add-product'}") // to secure and endpoint with role or permission
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Product> createProduct(
            @RequestPart("dto") CreateProductDTO dto,
            @RequestPart("imageFile") MultipartFile imageFile) {

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(productCategoryService.getProductCategoryById(dto.categoryId()));
        product.setStock(dto.stock());

        Product savedProduct = productService.createProduct(product, imageFile);
        return ResponseEntity.ok(savedProduct);
    }

    private record CreateProductDTO(String name, String description, double price, int categoryId, int stock) {
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> updateProduct(
            @RequestPart("dto") UpdateProductDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        Product product = new Product();
        product.setProductId(dto.id());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(productCategoryService.getProductCategoryById(dto.categoryId()));
        product.setStock(dto.stock());

        productService.updateProduct(product, imageFile);
        return ResponseEntity.ok("Product updated successfully");
    }

    private record UpdateProductDTO(int id, String name, String description, double price, int categoryId, int stock) {
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable int id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/category/{categoryId}")
    List<Product> getProductsByCategoryId(@PathVariable int categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    //http://localhost:8080/api/v1/products/filter?minPrice=0&maxPrice=200&sortDir=desc&page=1&size=3
    @GetMapping("/filter")
    ResponseEntity<Page<Product>> filterProducts(
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
