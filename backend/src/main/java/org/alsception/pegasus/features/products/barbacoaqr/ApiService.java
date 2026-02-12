package org.alsception.pegasus.features.products.barbacoaqr;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {  
    
    private final String[] urls =
            """
            https://api.master-fb.com/qr/clients/5285/1040/client_products
            https://api.master-fb.com/qr/clients/5285/1082/client_products
            https://api.master-fb.com/qr/clients/5285/174/client_products
            https://api.master-fb.com/qr/clients/5285/1740/client_products
            https://api.master-fb.com/qr/clients/5285/2/client_products
            https://api.master-fb.com/qr/clients/5285/2200/client_products
            https://api.master-fb.com/qr/clients/5285/456/client_products
            https://api.master-fb.com/qr/clients/5285/459/client_products
            https://api.master-fb.com/qr/clients/5285/525/client_products
            https://api.master-fb.com/qr/clients/5285/550/client_products
            https://api.master-fb.com/qr/clients/5285/645/client_products
            https://api.master-fb.com/qr/clients/5285/647/client_products
            https://api.master-fb.com/qr/clients/5285/647/client_products
            https://api.master-fb.com/qr/clients/5285/681/client_products
            https://api.master-fb.com/qr/clients/5285/7/client_products
            https://api.master-fb.com/qr/clients/5285/75/client_products
            https://api.master-fb.com/qr/clients/5285/76/client_products
            https://api.master-fb.com/qr/clients/5285/764/client_products
            https://api.master-fb.com/qr/clients/5285/8/client_products
            https://api.master-fb.com/qr/clients/5285/1009/client_products
            https://api.master-fb.com/qr/clients/5285/627/client_products
            https://api.master-fb.com/qr/clients/5285/361/client_products
            https://api.master-fb.com/qr/clients/5285/361/client_products
            https://api.master-fb.com/qr/clients/5285/1226/client_products
            https://api.master-fb.com/qr/clients/5285/366/client_products
            https://api.master-fb.com/qr/clients/5285/1226/client_products
            https://api.master-fb.com/qr/clients/5285/366/client_products
            https://api.master-fb.com/qr/clients/5285/653/client_products
            https://api.master-fb.com/qr/clients/5285/655/client_products
            https://api.master-fb.com/qr/clients/5285/426/client_products
            https://api.master-fb.com/qr/clients/5285/1040/client_products""".split("\\r?\\n");

    private final RestTemplate restTemplate;
    private final BRBProductRepository repository;
    
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    
    public ApiService(RestTemplate restTemplate, BRBProductRepository repository) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }
    
    public int fetchAndSaveProducts() {
        logger.warn("========================================");
        logger.warn("Initializing barbacoa master-fb sync");
        logger.warn("========================================");
        
        int totalSaved = 0;        
      
        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            try {
                logger.info("Processing URL {}/{}: {}", i + 1, urls.length, url);
                
                // Poziv eksternog API-ja
                BRBProduct[] products = restTemplate.getForObject(url, BRBProduct[].class);
                
                if (products == null || products.length == 0) {
                    logger.warn("  ⚠ No products returned from URL {}/{}", i + 1, urls.length);
                    continue;
                }
                
                // Čuvanje u bazu
                List<BRBProduct> savedProducts = repository.saveAll(Arrays.asList(products));
                int savedCount = savedProducts.size();
                totalSaved += savedCount;
                
                logger.info("  ✓ Successfully saved {} products from URL {}/{}", 
                           savedCount, i + 1, urls.length);
                
            } catch (Exception e) {
                logger.error("  ✗ Error processing URL {}/{}: {} - {}", 
                            i + 1, urls.length, url, e.getMessage());
            }
        }
        
        logger.warn("========================================");
        logger.warn("BRB Sync completed!");
        logger.warn("Total products saved: {}", totalSaved);
        logger.warn("========================================");

        return totalSaved;
    }
}