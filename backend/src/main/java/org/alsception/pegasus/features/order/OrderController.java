package org.alsception.pegasus.features.order;

import java.security.Principal;
import java.util.List;

import org.alsception.pegasus.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    //TODO: FIXX ERROR MESSAGE AND METHOD WHEN 
    //[org.springframework.web.bind.MissingServletRequestParameterException: Required request parameter 'userId' for method parameter type Long is not present]
    @GetMapping("/{id}")
    public ResponseEntity<PGSOrder> getOrderByUser(Principal principal, @PathVariable Long id) 
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        logger.debug("Get order ["+id+"] for user: " + username);
        
        PGSOrder order;
        try 
        {
            order = orderService.getById(id);
            return ResponseEntity.ok(order);
        }
        catch (BadRequestException e) 
        {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<PGSOrder>> find(Principal principal, @RequestParam(required = false) String search) 
    {
        //TODO: IF ADMIN, HE SHOULD SEE ALL ORDERS OF ALL USERS,
        // OTHERWISE, ONLY HIS ORDERS
        // AND SEARCH FOR:
        // DATE TO-FROM, AMOUNT, USER...
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();        

        logger.debug("Getting orders for: " + username + ", search: ["+search+"]");
        
        List<PGSOrder> orders = orderService.getByUsername(username, search);
        
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) 
    {
        try
        {        
            //First we must be sure that the user requesting deletion is the owner, or admin.
            //For now, we just assume it...
            orderService.delete(id);
            logger.info("Deleted order "+id);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e)
        {
            //If id is null, exception will be thrown...
            logger.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }        
    }
}