package org.alsception.pegasus.features.order;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.alsception.pegasus.core.exception.BadRequestException;
import org.alsception.pegasus.features.order.dto.OrderDTO;
import org.alsception.pegasus.features.order.dto.OrderMapper;
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
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /* =========================
       GET LIST
       ========================= */

    @GetMapping
    public ResponseEntity<List<OrderDTO>> find(
            Principal principal,
            @RequestParam(required = false) String search) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        logger.debug("Getting orders for: {}, search: [{}]", username, search);

        List<OrderDTO> result = orderService
                .getByUsername(username, search)
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /* =========================
       UPDATE
       ========================= */

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderDTO dto,
            Principal principal) 
    {

        logger.debug("Updating order {}", id);
        //logger.debug(dto.toString()); //OVDE exception

        try 
        {
            OrderDTO updated = orderService.update(id, dto);

            logger.info("Updated order {}", id);
            
            return ResponseEntity.ok(updated);
        }
        catch (Exception e) 
        {
            logger.error("Error updating order: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
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
