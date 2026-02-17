package org.alsception.pegasus.features.products;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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
    
    //Trenutno zovemo ovaj sa frontenda
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
    
    //Ovo ne koristimo sad
    @GetMapping("/p")
    public PaginatedProductsResponse getProductsWithPagination(
            @RequestParam(required = false) String search, 
            @RequestParam(required = false) String code, 
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "250") int size)
    {        
        logger.debug("getProductsWithPagination: search=["+search+"] code=["+code+"], name=["+name+"], page=["+page+"], size=["+size+"]");
        return productService.findProductsWithPagination(search, code, name, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PGSProductDTO> getProductById(@PathVariable Long id) 
    {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    //TODO: ovde cemo dodati most popular products
//    @GetMapping("/popular")
//    public PopularProductsWrapper getPopularProducts() 
//    {
//        return productService.getPopularProducts();
//    }
//    
    @PostMapping
    public PGSProduct createProduct(@RequestBody PGSProduct product) 
    {
        return productService.createProduct(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PGSProduct> updateProduct(@PathVariable Long id, @RequestBody PGSProduct p) 
    {
        if(id==0l)
        {
            try {
                logger.debug("received new product: ");
                logger.debug(p.toString());
                return ResponseEntity.ok(productService.createProduct(p));
            } 
            catch (Exception e) 
            {
                logger.error("Greska prilikom kreiranja novog proizvoda", e);
                return ResponseEntity.internalServerError().build();
            }
        }
        try {
            logger.debug("received product: ");
            logger.debug(p.toString());
            PGSProduct product = productService.updateProduct(id, p);
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