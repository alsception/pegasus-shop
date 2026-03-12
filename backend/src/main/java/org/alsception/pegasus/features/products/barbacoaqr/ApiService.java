package org.alsception.pegasus.features.products.barbacoaqr;

import java.util.Arrays;
import java.util.List;

import org.alsception.pegasus.features.order.OrderRepository;
import org.alsception.pegasus.features.order.OrderService;
import org.alsception.pegasus.features.order.PGSOrder;
import org.alsception.pegasus.features.products.PGSProduct;
import org.alsception.pegasus.features.products.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {  
    
    private final RestTemplate restTemplate;
    private final BRBProductRepository brbpRepository;    
    private final ProductRepository pgspRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final BRBProductToPGSProductMapper mapper;
    
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

    

    
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    
    public ApiService(
        RestTemplate restTemplate, 
        BRBProductRepository brbpRepository, 
        BRBProductToPGSProductMapper mapper,
        ProductRepository pgspRepository,
        OrderRepository orderRepository,
        OrderService ordersService) 
    {
        this.brbpRepository = brbpRepository;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.pgspRepository = pgspRepository;
        this.orderRepository = orderRepository;
        this.orderService = ordersService;
    }
    
    public int fetchAndSaveProducts() 
    {
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
                List<BRBProduct> savedProducts = brbpRepository.saveAll(Arrays.asList(products));
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
    
    public int processQRMasterProducts()
    {
        //1. Get all qr master products
        //2. for each qr product map to pgs product
        //3. save
        
        logger.warn("========================================");
        logger.warn("Initializing masterqr sync");
        logger.warn("========================================");
        
        int totalSaved = 0;   
        
        List<BRBProduct> lp = brbpRepository.findAll();
        List<PGSProduct> pgsProducts = mapper.mapList(lp);
        
        pgspRepository.saveAll(pgsProducts);
        
        logger.warn("masterqr sync done");
        
        return totalSaved;        
    }

    public int processOrders()
    {
        //1. Get all qr master products
        //2. for each qr product map to pgs product
        //3. save
        
        logger.warn("========================================");
        logger.warn("Initializing orders price processing");
        logger.warn("========================================");
        
        int totalSaved = 0;   
        
        List<PGSOrder> l = orderRepository.findAllWithItems();
        
        l = orderService.processPrice(l);
        
        orderRepository.saveAll(l);
        
        logger.warn("Order price calc done. Processed orders: "+l.size());
        
        return totalSaved;        
    }
}