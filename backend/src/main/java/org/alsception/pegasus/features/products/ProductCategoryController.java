package org.alsception.pegasus.features.products;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product-categories")
@CrossOrigin(origins = "*")
public class ProductCategoryController 
{
    private final ProductCategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryController.class);

    public ProductCategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<PGSProductCategory> getAllCategories() 
    {
        logger.debug("Getting product categories");
        return categoryService.getAllCategories();
    }

    // returns categories with their children
    @GetMapping("/tree")
    public List<PGSProductCategory> getCategoryTree() 
    {
        logger.debug("Getting product categories tree");
        return categoryService.getCategoryTree();
    }
}