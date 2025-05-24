package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.entity.ProductCategory;
import gov.iti.jet.ewd.ecom.exception.ProductCategoryNotFoundException;
import gov.iti.jet.ewd.ecom.repository.ProductCategoryRepository;
import gov.iti.jet.ewd.ecom.service.ProductCategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }


    @Override
    public Optional<ProductCategory> getProductCategoryByName(String name) {
        return Optional.ofNullable(productCategoryRepository.findByName(name)
                .orElseThrow(() -> new ProductCategoryNotFoundException("Product Category Not Found")));
    }

    @Override
    public List<ProductCategory> getAllProductCategories() {
        return productCategoryRepository.findAll();
    }

    @Override
    public ProductCategory createProductCategory(String name) {
        return productCategoryRepository.save( new ProductCategory(name));
    }

    @Override
    public ProductCategory updateProductCategory(ProductCategory productCategory) {
        ProductCategory existingCategory = productCategoryRepository.findById(productCategory.getId())
                .orElseThrow(() -> new ProductCategoryNotFoundException("Product category not found with ID: " + productCategory.getId()));

        existingCategory.setName(productCategory.getName());

        return productCategoryRepository.save(existingCategory);
    }

    @Override
    public boolean productCategoryExist(String name) {
        return productCategoryRepository.existsByName(name);
    }

    @Transactional
    @Override
    public boolean removeProductCategoryByName(String name) {
        if (!productCategoryRepository.existsByName(name)) {
            throw new ProductCategoryNotFoundException("Category not found: " + name);
        }
        return productCategoryRepository.deleteByName(name) > 0;
    }


    @Override
    public ProductCategory getProductCategoryById(int id) {
        return productCategoryRepository.findById(id).orElseThrow(()-> new ProductCategoryNotFoundException("Product Category Not Found"));
    }
}
