package org.alsception.pegasus.features.products;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductService 
{
    private static final int MAX_RATING = 5;
    
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final Random random = new Random();
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
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
        logger.debug("findProducts: search=["+search+"] code=["+code+"], name=["+name+"]");
        if(search != null)
        { 
            try
            {
                List<PGSProduct> result = productRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(search, search);
                
                logger.debug("Found products: "+result.size());
                logger.debug("Converting to dto");
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
                return productRepository.findAll().stream()
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
    
    public PGSProduct createProduct(PGSProduct product) 
    {       
        //1) save audit: saving product... 
        
        //This checks for code and other business logic constraints. Will thorw exception if product not valid  
        ProductValidator.validate(product);
        
        
        final List<PGSReview> reviews = product.getReviews();
        
        //New product with reviews (needs to be saved separately)
        if(product.getId() == null && reviews!=null && !reviews.isEmpty())
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
        ProductValidator.validate(updatedProduct);
        
        /*calculateUsdPrice(updatedProduct);       */
        
        return productRepository.findById(id).map(existingProduct -> {

            // Keep existing reviews if updatedProduct does not explicitly contain reviews
            /*if (updatedProduct.getReviews() == null) {
                updatedProduct.setReviews(existingProduct.getReviews());
            }*/

            // If updatedProduct has an empty list of reviews, remove all reviews
            /*if (updatedProduct.getReviews() != null && updatedProduct.getReviews().isEmpty()) {
                existingProduct.getReviews().clear();
            }*/
            
            

            // Update other fields
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCode(updatedProduct.getCode());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setBasePrice(updatedProduct.getBasePrice());
            existingProduct.setActive(updatedProduct.isActive());
            existingProduct.setBaseCurrency(updatedProduct.getBaseCurrency());
            existingProduct.setBrand(updatedProduct.getBrand());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setComment(updatedProduct.getComment());
            existingProduct.setDiscount(updatedProduct.getDiscount());
            existingProduct.setDiscountType(updatedProduct.getDiscountType());
            existingProduct.setImageUrl(updatedProduct.getImageUrl());
            existingProduct.setHeightCm(updatedProduct.getHeightCm());
            existingProduct.setLengthCm(updatedProduct.getLengthCm());
            existingProduct.setMarked(updatedProduct.getMarked());
            existingProduct.setOther(updatedProduct.getOther());
            existingProduct.setShippingCost(updatedProduct.getShippingCost());
            existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
            existingProduct.setTaxPercent(updatedProduct.getTaxPercent());
            //existingProduct.setTaxPercent(updatedProduct.getTaxAmount());//Amount is calculated
            existingProduct.setUnit(updatedProduct.getUnit());
            //existingProduct.setVersion(updatedProduct.getVersion());
            existingProduct.setWeightKg(updatedProduct.getWeightKg());
            existingProduct.setWidthCm(updatedProduct.getWidthCm());

            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private PGSProduct createProductAndReviews(PGSProduct product) 
    {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        // Ensure reviews is never null to avoid NullPointerException
        List<PGSReview> reviews = Optional.ofNullable(product.getReviews()).orElse(Collections.emptyList());
        product.setReviews(null);

        // Save product first
        final PGSProduct savedProduct = productRepository.save(product);

        // Associate reviews with saved product and batch save
        reviews.forEach(r -> r.setProduct(savedProduct));
        if (!reviews.isEmpty()) {
            reviewRepository.saveAll(reviews);
        }

        // Reload product with reviews from DB
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
    
    public PopularProductsWrapper getPopularProducts() 
    {
        List<Object[]> pproducts = productRepository.getPopularProducts();
    
        List<PopularProduct> output = pproducts.stream()
        .map(obj -> new PopularProduct(
            (String) obj[1],    // Product Name
            (Double) obj[2]     // Average Rating
        ))
        .collect(Collectors.toList());
        
        return new PopularProductsWrapper(output);
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
                    product.setCategory(1l);
                    product.setActive(Boolean.TRUE);
                    product.setStockQuantity(random.nextInt(1000) + 10);
                    product.setShippingCost(BigDecimal.ZERO);
                    product.setTaxPercent(BigDecimal.ZERO);
                    generateRandomReviews(product);
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
                product.setCategory(1l);
                product.setActive(Boolean.TRUE);
                product.setStockQuantity(random.nextInt(1000) + 10);
                product.setShippingCost(BigDecimal.ZERO);
                product.setTaxPercent(BigDecimal.ZERO);

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

    private void generateRandomReviews(PGSProduct product) 
    {
        int reviewCount = random.nextInt(4) + 2; // Generates a random number between 2 and 5
        List<PGSReview> reviews = new ArrayList<>();

        for (int i = 0; i < reviewCount; i++) 
        {
            PGSReview review = new PGSReview();
            review.setReviewer("Reviewer " + (i + 1));
            review.setText("This is a review " + (i + 1) + " for " + product.getName());
            review.setRating(random.nextInt(MAX_RATING) + 1); // Random rating from 1 to 5
            review.setProduct(product);
            reviews.add(review);
        }
        
        product.setReviews(reviews);
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