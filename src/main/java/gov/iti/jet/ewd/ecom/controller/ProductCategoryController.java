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
    ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    // Get all categories
    @GetMapping
    List<ProductCategory> getAllProductCategories() {
        return productCategoryService.getAllProductCategories();
    }

    // Get category by ID
    @GetMapping("/{id}")
    ProductCategory getCategoryById(@PathVariable int id) {
        return productCategoryService.getProductCategoryById(id);
    }

    // Get category by name
    @GetMapping("/name/{name}")
    ProductCategory getCategoryByName(@PathVariable String name) {
        return productCategoryService.getProductCategoryByName(name).orElse(null);
    }

    // Check if category exists by name
    @GetMapping("/exists/{name}")
    boolean categoryExists(@PathVariable String name) {
        return productCategoryService.productCategoryExist(name);
    }

    // Create a new category.
    @PostMapping
    ProductCategory createCategory(@RequestBody CreateCategoryDTO dto) {
        if (dto.name() == null) {
            throw new IllegalArgumentException("Category name must be provided");
        }
        if (!dto.name().trim().equals(dto.name())) {
            throw new IllegalArgumentException("Category name cannot be empty or contain leading or trailing spaces");
        }

        return productCategoryService.createProductCategory(dto.name());
    }

    private record CreateCategoryDTO(String name) {
    }

    @PutMapping
    ResponseEntity<String> updateCategory(@RequestBody ProductCategory category) {
        productCategoryService.updateProductCategory(category);
        return ResponseEntity.ok("Category updated successfully.");
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteCategoryById(@PathVariable int id) {
        boolean isDeleted = productCategoryService.removeProductCategoryById(id);
        if (isDeleted) {
            return ResponseEntity.ok("Category deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete category.");
        }
    }

    // Delete category by name
    @DeleteMapping("name/{name}")
    ResponseEntity<String> deleteCategory(@PathVariable String name) {
        boolean deleted = productCategoryService.removeProductCategoryByName(name);
        if (deleted) {
            return ResponseEntity.ok("Category deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete category.");
        }
    }
}
