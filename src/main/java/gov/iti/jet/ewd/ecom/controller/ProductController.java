package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.entity.Product;
import gov.iti.jet.ewd.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
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
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product product) {
        product.setProductId(id);
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
}
