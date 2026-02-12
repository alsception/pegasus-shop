package org.alsception.pegasus.features.products.barbacoaqr;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BRBProductService {
    
    private final RestTemplate restTemplate;
    private final BRBProductRepository productRepository;
    
    public List<BRBProduct> fetchAndSaveProducts(String apiUrl) {
        // Poziv API-ja
        BRBProduct[] products = restTemplate.getForObject(apiUrl, BRBProduct[].class);
        
        // Čuvanje u bazu
        List<BRBProduct> savedProducts = productRepository.saveAll(Arrays.asList(products));
        
        return savedProducts;
    }
}