package org.alsception.pegasus.features.products;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.alsception.pegasus.features.products.PopularProductsWrapper;
import org.alsception.pegasus.features.products.PGSProduct;
import org.alsception.pegasus.core.security.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*") // Allow all origins
@RequestMapping("/api/products")
public class ProductController 
{
    @Autowired
    private ProductService productService;
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    //Not used now, we use paginated insted
    @GetMapping
    public List<PGSProductDTO> getProducts(
            @RequestParam(required = false) String search, 
            @RequestParam(required = false) String code, 
            @RequestParam(required = false) String name)
    {        
        logger.debug("getProducts: search=["+search+"] code=["+code+"], name=["+name+"]");
        List<PGSProductDTO> products = productService.findProducts(search, code, name);
        return products;
    }
    
    //Currently we use this on frontend
    @GetMapping("/p")
    public PaginatedProductsResponse getProductsWithPagination(
            @RequestParam(required = false) String search, 
            @RequestParam(required = false) String code, 
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {        
        logger.debug("getProducts: search=["+search+"] code=["+code+"], name=["+name+"], page=["+page+"], size=["+size+"]");
        return productService.findProductsWithPagination(search, code, name, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PGSProductDTO> getProductById(@PathVariable Long id) 
    {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/popular")
    public PopularProductsWrapper getPopularProducts() 
    {
        return productService.getPopularProducts();
    }
    
    @PostMapping
    public PGSProduct createProduct(@RequestBody PGSProduct product) 
    {
        return productService.createProduct(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PGSProduct> updateProduct(@PathVariable Long id, @RequestBody PGSProduct updatedProduct) 
    {
        try {
            logger.debug("received product: ");
            logger.debug(updatedProduct.toString());
            PGSProduct product = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(product);
        } 
        catch (EntityNotFoundException e) 
        {
            return ResponseEntity.notFound().build();
        }
    }
        
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) 
    {
        if (productService.existsById(id)) 
        {        
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}