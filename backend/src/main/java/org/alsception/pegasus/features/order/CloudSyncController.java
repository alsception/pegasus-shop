package org.alsception.pegasus.features.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync/orders")
@Profile("local") // Aktiviraj samo na cloudu? Ovaj prima podatke
public class CloudSyncController {

    @Autowired
    private OrderRepository orderRepository;

    @Value("${sync.api.key}")
    private String secretKey;

    private static final Logger logger = LoggerFactory.getLogger(CloudSyncController.class);

    @PostMapping()
    public ResponseEntity<String> receiveOrder(
            @RequestHeader("X-API-KEY") String receivedKey,
            @RequestBody PGSOrder incomingOrder) 
    {
        logger.info("Incoming order received");

        // Provjera ključa
        if (!secretKey.equals(receivedKey)) 
        {
            logger.info("Unauthorized, bad X-API-KEY");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Resetiraj 'synced' na true jer je već na cloudu
        incomingOrder.setSynced(true);

        // Spasimo narudžbu u cloud bazu (id bi trebao biti isti ili generiran ovisno o logici)
        orderRepository.save(incomingOrder);

        logger.info("Incoming order saved");

        return ResponseEntity.ok("Sinkronizirano!");
    }
}