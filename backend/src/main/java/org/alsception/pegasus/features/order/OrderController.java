package org.alsception.pegasus.features.order;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.alsception.pegasus.core.exception.BadRequestException;
import org.alsception.pegasus.features.order.dto.OrderDTO;
import org.alsception.pegasus.features.order.dto.OrderMapper;
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.users.PGSUserRole;
import org.alsception.pegasus.features.users.UserService;
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
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService,UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /* =========================
       GET ONE
       ========================= */

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderByUser(
            Principal principal,
            @PathVariable Long id) 
    {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        logger.debug("Get order [{}] for user: {}", id, username);

        try 
        {
            PGSOrder order = orderService.getById(id);
            return ResponseEntity.ok(OrderMapper.toDto(order));
        } 
        catch (BadRequestException e) 
        {
            //Ovo ne bi trebalo vise da se pojavljuje
            logger.warn("ERR_50: "+e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /* =========================
       GET LIST
       ========================= */

    @GetMapping
    public ResponseEntity<List<OrderDTO>> find(
            Principal principal,
            @RequestParam(required = false) String search) 
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        PGSUser requestor = userService.findByUsername(username);
        logger.debug("Getting orders for: {}, search: [{}]", username, search);
        List<OrderDTO> result;
        
        //Moze da vidi sve
        if( requestor.getRole().equals(PGSUserRole.ADMIN) || requestor.getRole().equals(PGSUserRole.KITCHEN) )
        {
            result = orderService
                .get( search )
                .stream()
                .map( OrderMapper::toDto )
                .collect(Collectors.toList());
        }
        else //moze da vidi samo svoje
        {
            result = orderService
                .getByUsername( username, search )
                .stream()
                .map( OrderMapper::toDto )
                .collect(Collectors.toList());
        }      

        return ResponseEntity.ok(result);
    }

    /* =========================
       UPDATE
       ========================= */
    
    //TODO: IF ADMIN, HE SHOULD SEE/EDIT ALL ORDERS OF ALL USERS,
        // OTHERWISE, ONLY HIS ORDERS
        // AND SEARCH FOR:
        // DATE TO-FROM, AMOUNT, USER...

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderDTO dto,
            Principal principal) 
    {
        logger.debug("Updating order {}", id);

        try 
        {
            OrderDTO updated = orderService.update(id, dto);

            logger.info("Order updated {}", id);
            
            return ResponseEntity.ok(updated);
        }
        catch (Exception e) 
        {
            logger.error("Error updating order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                                .body("Error updating order: " + e.getMessage());
        }
    }

    @PutMapping(value = "/update-status/{id}", consumes = "application/json")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Principal principal) 
    {
        logger.debug("Updating order status {}", id);

        try 
        {
            orderService.updateOrderStatus(id, status);

            return ResponseEntity.ok().build();
        }
        catch (Exception e) 
        {
            logger.error("Error updating order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                                .body("Error updating order: " + e.getMessage());
        }
    }

    /* =========================
       DELETE
       ========================= */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Principal principal) 
    {

        try 
        {
            //First we must be sure that the user requesting deletion is the owner, or admin.
            //For now, we just assume it...
            
            orderService.delete(id);
            logger.info("Deleted order {}", id);
            return ResponseEntity.noContent().build();
        } 
        catch (Exception e) 
        {
            logger.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
