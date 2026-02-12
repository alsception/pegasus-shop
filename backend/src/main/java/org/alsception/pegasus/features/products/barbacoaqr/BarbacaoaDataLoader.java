package org.alsception.pegasus.features.products.barbacoaqr;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BarbacaoaDataLoader implements CommandLineRunner {
    
    private final ApiService apiService;
    
    @Override
    public void run(String... args) 
    {
        if( false )
        {
            apiService.fetchAndSaveProducts();
        }
    }
}