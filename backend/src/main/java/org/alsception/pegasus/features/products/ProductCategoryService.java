package org.alsception.pegasus.features.products;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductCategoryService 
{
    private final ProductCategoryRepository categoryRepository;

    public ProductCategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<PGSProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Returns only root categories with their children populated
    public List<PGSProductCategory> getCategoryTree() {
        return categoryRepository.findByParentIsNull();
    }
}