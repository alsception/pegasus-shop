package org.alsception.pegasus.features.order;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("local") // Aktiviraj samo na lokalu, ovaj šalje podatke
public class LocalSyncService {

    @Autowired
    private OrderRepository orderRepository;

    @Value("${cloud.api.url}") // URL tvog cloud servera
    private String cloudUrl;

    @Value("${sync.api.key}") // Tvoj tajni ključ
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(LocalSyncService.class);


    @Scheduled(fixedDelay = 10000) // Svakih 10 sekundi
    public void pushToCloud()
    {
       /*
        * E sad, nebi trebalo ovde da saljemo user korisnika, dovoljno je samo ime. (todo)
        */


        logger.info("Initiating cloud sync");
        List<PGSOrder> unsynced = orderRepository.findBySyncedFalseWithItems();

        logger.info("Found unsynced orders: "+unsynced.size());
        for (PGSOrder order : unsynced) 
        {
            try 
            {
                // Postavljanje headera sa tajnim ključem
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-API-KEY", apiKey);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<PGSOrder> request = new HttpEntity<>(order, headers);

                // Slanje na Cloud
                logger.info("Sending order "+order.getId() + " " + cloudUrl + "/api/sync/orders");
                restTemplate.postForEntity(cloudUrl + "/api/sync/orders", request, String.class);

                // Ako je uspjelo, označi kao sinkronizirano
                order.setSynced(true);
                orderRepository.save(order);

                logger.info("Order sent");
            } 
            catch (Exception e) 
            {
                logger.error("Greska prilikom sinhronizacije narudzbi: " + e.getMessage());
                break; // Prekini petlju do idućeg pokušaja
            }
        }
    }
}