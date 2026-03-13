package org.alsception.pegasus.features.products;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Service
public class ProductService 
{   
    private final ProductRepository productRepository;
    private final Random random = new Random();
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<PGSProduct> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<PGSProductDTO> getProductById(Long id) 
    {
        return productRepository.findById(id)
            .map(PGSProductDTO::new); // converts entity to DTO if found
    }
    
    public List<PGSProductDTO> findProducts(String search, String code, String name) 
    {
        logger.debug("findProducts: search=["+search+"] code=["+code+"], name=["+name+"]. /46");
        if(search != null)
        { 
            try
            {
                List<PGSProduct> result = productRepository.searchActiveProducts(search);
                
                logger.debug("Found products: "+result.size());
                logger.debug("Converting to dto...");
                
                return  result.stream()
                            .map(PGSProductDTO::new)
                            .toList();
            }
            catch(Exception e)
            {
                logger.error("Error searching products: "+e.getMessage());
                return null;
            }
        }
        else
        {
            if(code==null && name == null)
            { 
                return productRepository.findByActiveTrue().stream()
                            .map(PGSProductDTO::new)
                            .toList();
            }
            else
            {
                return productRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(code, name).stream()
                            .map(PGSProductDTO::new)
                            .toList();
            }
        }
    }
    
    public PaginatedProductsResponse findProductsWithPagination(String search, String code, String name, int page, int size) 
    {
        logger.debug("Looking for products...");
        Pageable pageable = PageRequest.of(page, size);
        Page<PGSProduct> productPage;
        
        //TODO: ovde moramo izvuci samo products koji us active = true;
        //A zatim dodati drugi metod za prikazivanje i sakrivenih

        if (search != null && !search.isEmpty()) {
            productPage = productRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(search, search, pageable);
        } else if (code == null && name == null) {
            productPage = productRepository.findAll(pageable);
        } else {
            productPage = productRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(code, name, pageable);
        }

        logger.debug("Found products: "+productPage.getNumberOfElements());
        
        //Why page to list and then to page???
        
        List<PGSProductDTO> products = productPage.getContent().stream()
            .map(PGSProductDTO::new)
            .toList();

        //logger.debug("Found products: "+products.size());
        /*products.forEach(item -> logger.trace("Item: " + item));*/
        
        return new PaginatedProductsResponse(
            products,
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            page,
            size
        );
    }
    
    public PGSProduct createProduct(PGSProduct product) 
    {       
        //1) save audit: saving product... 
                
        //New product with reviews (needs to be saved separately)
        if(product.getId() == null)
        {
            return createProductAndReviews(product);
        }
        else
        {
            return productRepository.save(product);
        }        
    }
    
    public PGSProduct updateProduct(Long id, PGSProduct updatedProduct) 
    {
        
        return productRepository.findById(id).map(existingProduct -> 
        {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCode(updatedProduct.getCode());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setBasePrice(updatedProduct.getBasePrice());
            existingProduct.setActive(updatedProduct.isActive());
            existingProduct.setBaseCurrency(updatedProduct.getBaseCurrency());
            existingProduct.setBrand(updatedProduct.getBrand());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setComment(updatedProduct.getComment());
            existingProduct.setImageUrl(updatedProduct.getImageUrl());
            existingProduct.setMarked(updatedProduct.getMarked());
            existingProduct.setOther(updatedProduct.getOther());
            existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
            existingProduct.setUnit(updatedProduct.getUnit());

            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private PGSProduct createProductAndReviews(PGSProduct product) 
    {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        // Save product first
        final PGSProduct savedProduct = productRepository.save(product);

        // Reload productfrom DB
        return productRepository.getReferenceById(savedProduct.getId());
    }
    
    public void deleteProduct(Long id) 
    {
        productRepository.deleteById(id);
    }

    public boolean existsById(Long id) 
    {
        return productRepository.existsById(id);
    } 
        
    public void generateSampleProducts(int count) 
    {
        if (productRepository.count() == 0 && count <= 1000) 
        { 
            int errors = 0;
            for (int i = 1; i <= count; i++) 
            {
                try{
                    logger.info("Creating sample product "+i);
                    PGSProduct product = new PGSProduct();
                    product.setCode(generateValidCode());
                    product.setName("Product " + i);
                    product.setBasePrice(BigDecimal.valueOf(random.nextInt(1000) + 10)); // Price: 10 - 109 EUR
                    product.setBaseCurrency("EUR"); // Price: 10 - 109 EUR
                    product.setDescription("Description for product " + i);
                    product.setBrand("xxx");
                    product.setActive(Boolean.TRUE);
                    product.setStockQuantity(random.nextInt(1000) + 10);
                    createProduct(product);
                }
                catch(Exception e)
                {
                    logger.error("Error generationg product "+i);
                    logger.error(e.getMessage());
                    errors++;
                }
            }
            
            logger.info("✅ Sample products initialized: "+ count);
            logger.info("Errors: "+errors);
        }
        else
        {
            logger.info("Sample products not created. Product repository already contains some products or illegal count requested.");
        }
    }
    
    public List<PGSProductDTO> generateRandomMockProducts(int count) 
    {
        List<PGSProductDTO> plist = new ArrayList<>();
        
        int errors = 0;
        for (int i = 1; i <= count; i++) 
        {
            try{
                PGSProduct product = new PGSProduct();
                product.setCode(generateValidCode());
                product.setName("Product " + i);
                product.setBasePrice(BigDecimal.valueOf(random.nextInt(1000) + 10)); // Price: 10 - 109 EUR
                product.setBaseCurrency("EUR"); // Price: 10 - 109 EUR
                product.setDescription("Description for product " + i);
                product.setBrand("xxx");
                product.setActive(Boolean.TRUE);
                product.setStockQuantity(random.nextInt(1000) + 10);

                PGSProductDTO pp = new PGSProductDTO(product);
                plist.add(pp);
            }
            catch(Exception e)
            {
                logger.error("Error generationg product "+i);
                logger.error(e.getMessage());
                errors++;
            }
        }

        logger.info("✅ " + count + " Sample random mock products initialized.");
        logger.info("Errors: "+errors);

        return plist;
    }

    private String generateValidCode() {
        StringBuilder sb = new StringBuilder("PROD-");
        for (int i = 0; i < 10; i++) 
        {
            sb.append(random.nextInt(10)); // Generate 15-digit numeric code
        }
        return sb.toString();
    }
}