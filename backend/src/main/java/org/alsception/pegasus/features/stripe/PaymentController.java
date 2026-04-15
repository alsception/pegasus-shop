package org.alsception.pegasus.features.stripe;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.alsception.pegasus.core.exception.BadRequestException;
import org.alsception.pegasus.features.order.OrderService;
import org.alsception.pegasus.features.order.PGSOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    // Iz app properties
    @Value("${STRIPE.SECRET.KEY}")
    private String stripeSecretKey;

    @Value("${STRIPE.WEBHOOK.SECRET}")
    private String endpointSecret;
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create-payment-intent")
    public Map<String, String> createPaymentIntent(@RequestBody PaymentRequest req) throws StripeException 
    {
        logger.info("received payment request");
        
        Stripe.apiKey = stripeSecretKey;
        long amount = 0L; 

        //1. load order from db and calcluate amount
        try 
        {
            PGSOrder order = orderService.getById(req.getOrderId());
            logger.debug("order loaded");            

            amount = order.getPrice().movePointRight(2) //ovde je kjucno puta 100 jer je amount u centima,Primer: 2000L je 20.00 EUR
                   .longValue();

            logger.debug("amount:"+amount);            
        } 
        catch (BadRequestException e) 
        {
            logger.warn("ERR_72: "+e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "true");
            response.put("message", e.getMessage());
            return response;
        }

        // 2. Kreiraj parametre sa metadata (da znaš koji je order kasnije)
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("eur")
                .putMetadata("order_id", req.getOrderId().toString())
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .build();

        // 3. Kreiraj intent i pošalji client_secret nazad Svelte-u
        PaymentIntent intent = PaymentIntent.create(params);
        
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());
        return response;
    }

    /***
     * TODO: OVDE NESTO NIJE DOBRO ZASAD NERADI
     * 
     * @param payload
     * @param sigHeader
     * @return
     */


    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Neuspela verifikacija");
        }

        Optional<StripeObject> obj = event.getDataObjectDeserializer().getObject();

        if (obj.isPresent()) {
            System.out.println(obj.toString());



            StripeObject stripeObject = obj.get();

            if (stripeObject instanceof Charge) {
                Charge charge = (Charge) stripeObject;

                if (charge.getPaid() && "succeeded".equals(charge.getStatus())) {
                    String orderId = charge.getMetadata().get("order_id");

                    logger.info("PLACENO: " + orderId);
                }
            }
        }
/* 
        if (obj.isEmpty()) {
            return ResponseEntity.ok("");
        }

        Object stripeObject = obj.get();

        // 🔥 UNIVERSALNO: ako je Charge → gledaj paid/status
        if (stripeObject instanceof Charge) {
            Charge charge = (Charge) stripeObject;

            if (charge.getPaid() && "succeeded".equals(charge.getStatus())) {
                String orderId = charge.getMetadata().get("order_id");

                logger.info("PLACENO: " + orderId);
            }
        } */

        return ResponseEntity.ok("");
    }
    /* @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) 
    {
        logger.info("received webhook request");
        Event event;
        
        try 
        {
            System.out.println(payload.toString());
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        }
        catch (SignatureVerificationException e) 
        {
            logger.error(e.getMessage());
            return ResponseEntity.status(400).body("Neuspela verifikacija");
        }

        // Ako je plaćanje prošlo
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
            String orderId = intent.getMetadata().get("order_id");
            
            // OVDE: Ažuriraj bazu (npr. orderRepo.markAsPaid(orderId))
            logger.info("Porudžbina " + orderId + " je plaćena!");
        }
        else
        {
            logger.warn("Neuspesno placanje");
        }

        return ResponseEntity.ok("");
    } */
}
