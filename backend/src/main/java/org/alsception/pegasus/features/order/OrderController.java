package org.alsception.pegasus.features.order;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alsception.pegasus.core.users.PGSUser;
import org.alsception.pegasus.features.order.OrderService;
import org.alsception.pegasus.core.users.UserService;
import org.alsception.pegasus.features.order.PGSCheckoutRequestDTO;
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
    private final UserService userService;
    private final PGSUser user = null;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }
    
    //TODO: FIXX ERROR MESSAGE AND METHOD WHEN 
    //[org.springframework.web.bind.MissingServletRequestParameterException: Required request parameter 'userId' for method parameter type Long is not present]
    @GetMapping("/{id}")
    public ResponseEntity<PGSOrder> getOrderByUser(Principal principal, @PathVariable Long id) 
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        logger.debug("Get order ["+id+"] for user: " + username);
        
        PGSOrder order = orderService.getById(id).get();
        
        return ResponseEntity.ok(order);
    }
    
    @GetMapping
    public ResponseEntity<List<PGSOrder>> getAll(Principal principal) 
    {
        //TODO: IF ADMIN, HE SHOULD SEE ALL ORDERS OF ALL USERS,
        // OTHERWISE, ONLY HIS ORDERS
        // AND SEARCH FOR:
        // DATE TO-FROM, AMOUNT, USER...
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();        

        logger.debug("Getting orders for: " + username);
        
        List<PGSOrder> orders = orderService.getByUsername(username);
        
        return ResponseEntity.ok(orders);
    }
}
