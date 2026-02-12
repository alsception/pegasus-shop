// Controller
package org.alsception.pegasus.features.products.barbacoaqr;

import java.time.LocalDateTime;
import java.util.HashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/barbacoa")
@RequiredArgsConstructor
public class BarbacaoaController {
    
    private final ApiService apiService;
    private final BRBProductRepository repository;
    private final BRBProductMapper mapper;
    
    // Sync proizvoda
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncProducts() 
    {
        int result = apiService.fetchAndSaveProducts();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sync completed successfully! Products imported: "+result);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
    
   // Dobavi sve proizvode sa opcionalnim pretraživanjem
    @GetMapping("/products")
    public ResponseEntity<List<BRBProductDTO>> getAllProducts(
        @RequestParam(required = false) String search) 
    {
    
        List<BRBProduct> products;

        if (search != null && !search.trim().isEmpty()) {
            products = repository.findByTitleContainingIgnoreCase(search.trim());
        } else {
            products = repository.findAll();
        }

        List<BRBProductDTO> dtos = mapper.toDTOList(products);
        return ResponseEntity.ok(dtos);
}
    
    // Dobavi proizvod po ID-u
    @GetMapping("/products/{id}")
    public ResponseEntity<BRBProductDTO> getProductById(@PathVariable Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Paginacija
    @GetMapping("/products/page")
    public ResponseEntity<Page<BRBProductDTO>> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<BRBProduct> productsPage = repository.findAll(pageable);
        Page<BRBProductDTO> dtoPage = productsPage.map(mapper::toDTO);
        
        return ResponseEntity.ok(dtoPage);
    }
    
//    // Pretraga po naslovu
//    @GetMapping("/products/search")
//    public ResponseEntity<List<BRBProductDTO>> searchByTitle(@RequestParam String title) {
//        // Dodaj ovu metodu u repository
//        List<BRBProduct> products = repository.findByTitleContainingIgnoreCase(title);
//        List<BRBProductDTO> dtos = mapper.toDTOList(products);
//        return ResponseEntity.ok(dtos);
//    }
    
//    // Filtriraj po cijeni
//    @GetMapping("/products/price-range")
//    public ResponseEntity<List<BRBProductDTO>> getByPriceRange(
//            @RequestParam Double minPrice,
//            @RequestParam Double maxPrice) {
//        
//        // Dodaj ovu metodu u repository
//        List<BRBProduct> products = repository.findByPriceBottleBetween(
//                java.math.BigDecimal.valueOf(minPrice), 
//                java.math.BigDecimal.valueOf(maxPrice));
//        List<BRBProductDTO> dtos = mapper.toDTOList(products);
//        return ResponseEntity.ok(dtos);
//    }
    
    // Broj proizvoda
    @GetMapping("/products/count")
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(repository.count());
    }
}