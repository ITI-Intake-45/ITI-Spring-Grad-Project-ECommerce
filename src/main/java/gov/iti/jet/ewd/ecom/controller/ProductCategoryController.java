package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.entity.ProductCategory;
import gov.iti.jet.ewd.ecom.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    // Get all categories
    @GetMapping
    public List<ProductCategory> getAllProductCategories() {
        return productCategoryService.getAllProductCategories();
    }

    // Get category by ID
    @GetMapping("/{id}")
    public ProductCategory getCategoryById(@PathVariable int id) {
        return productCategoryService.getProductCategoryById(id);
    }

    // Get category by name
    @GetMapping("/name/{name}")
    public ProductCategory getCategoryByName(@PathVariable String name) {
        return productCategoryService.getProductCategoryByName(name).orElse(null);
    }

    // Check if category exists by name
    @GetMapping("/exists/{name}")
    public boolean categoryExists(@PathVariable String name) {
        return productCategoryService.productCategoryExist(name);
    }

    // Create new category
    @PostMapping
    public ProductCategory createCategory(@RequestBody ProductCategory productCategory) {
        return productCategoryService.createProductCategory(productCategory.getName());
    }

    // Update category
    @PutMapping("/{id}")
    public ProductCategory updateCategory(@PathVariable int id, @RequestBody ProductCategory category) {
        category.setId(id);
        return productCategoryService.updateProductCategory(category);
    }

    // Delete category by name
    @DeleteMapping("name/{name}")
    public ResponseEntity<String> deleteCategory(@PathVariable String name) {
        boolean deleted = productCategoryService.removeProductCategoryByName(name);
        if (deleted) {
            return ResponseEntity.ok("Category deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete category.");
        }
    }
}
