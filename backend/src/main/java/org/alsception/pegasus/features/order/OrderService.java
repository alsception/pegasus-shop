package org.alsception.pegasus.features.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.alsception.pegasus.features.products.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.alsception.pegasus.core.exception.BadRequestException;
import org.alsception.pegasus.core.utils.CodeGenerator;
import org.alsception.pegasus.features.notifications.NotificationService;
import org.alsception.pegasus.features.order.dto.OrderDTO;
import org.alsception.pegasus.features.order.dto.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService 
{
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderService(
            ProductRepository productRepository, 
            OrderRepository orderRepository,
            NotificationService notificationService) 
    {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;        
    }
    
    /*
     * TODO: Check that user is 
     * 1. owner
     * 2. or admin (or webshop employee)     
     */
    
    public List<PGSOrder> getByUserId(Long userId) 
    {
        /*PGSUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));*/
        
        return processPrice(orderRepository.findByUserId(userId));
    }
    
    /*
     * Need transactional annotation to correctly load the orders, otherwise we'll get lazy loading exception
     */
    @Transactional(readOnly = true)
    public List<PGSOrder> getByUsername(String username, String search) 
    {    
        List<PGSOrder> orders;
        
        if(search == null || "".equals(search)) 
        {
            orders = orderRepository.findByUsernameWithItems(username);
        }
        else
        {
            orders = orderRepository.findByUsernameAndCodeOrTableWithItems(username,"%"+search.replace("-", "").toUpperCase()+"%");   //Need to remove dashes from code. And also make it case sens
        }

        //NEXT: MAKE SEARCH CASE INSENSITIVE

        return processPrice(orders);
    }
    
    @Transactional(readOnly = true)
    public List<PGSOrder> get(String search) 
    {    
        List<PGSOrder> orders;
       
        if(search == null || "".equals(search)) 
        {
            orders = orderRepository.findWithItems();
        }
        else
        {
            orders = orderRepository.findByCodeOrTableWithItems("%"+search.replace("-", "").toUpperCase()+"%");   //Need to remove dashes from code. And also make it case sens
        }

        //NEXT: MAKE SEARCH CASE INSENSITIVE

        return processPrice(orders);
    }
    
    
    /**
     *  Price should be calculated at the moment of order creation (checkout).
     *  This is just the temporary use for old orders missing
     */
    public List<PGSOrder> processPrice(List<PGSOrder> orders)
    {
        if(orders!=null && !orders.isEmpty())
        {
            orders.forEach(order -> 
            {   
                //fix code
                if(order.getCode()==null || "".equals(order.getCode()))
                {
                    order.setCode(CodeGenerator.toAlpha(order.getId()));
                }
                 
                BigDecimal total = order.getItems().stream()
                    .map(item -> 
                    {
                        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                        return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                    }).reduce(BigDecimal.ZERO, BigDecimal::add);

                order.setPrice(total);
            });
        }
        return orders;
    }
    
    public List<PGSOrder> getAll(String search) 
    {
        return orderRepository.findAll();              
    }
    
    @Transactional()
    public PGSOrder getById(Long id) throws BadRequestException
    {
        List<PGSOrder> orders = orderRepository.findByIdWithItems(id);
        if(orders.isEmpty()) throw new BadRequestException("Not found");
        orders = this.processPrice(orders);
        return orders.get(0);
    }
    
    public void delete(Long id)
    {
        this.orderRepository.deleteById(id);
    }

//    @Transactional
//    public PGSOrder update(Long id, PGSOrder receivedOrder) 
//    {
//        PGSOrder existingOrder = orderRepository.findById(id)
//            .orElseThrow(() -> new BadRequestException("Order not found"));
//
//        logger.debug("Fetched order status:");
//        logger.debug(existingOrder.getStatus());
//        // Update fields 
//        logger.debug("Received order status:");
//        logger.debug(receivedOrder.getStatus());
//        existingOrder.setStatus(receivedOrder.getStatus());
//
//        //Currently we only save Status, because the frontend doesnt send items list. TODO
//
//        /*existingOrder.setPrice(updatedOrder.getPrice());
//        existingOrder.setCode(updatedOrder.getCode());
//
//        // Update items collection safely
//        existingOrder.getItems().clear();
//        if (updatedOrder.getItems() != null) {
//            existingOrder.getItems().addAll(updatedOrder.getItems());
//        }*/
//
//        // Save and return updated order
//        return orderRepository.save(existingOrder);
//    }    
    /**
     * Ovaj metod se zapravo ne koristi prilikom promene statusa vec odgovarajuci ispod
     *
     */
    @Transactional()
    private PGSOrder update(Long id, PGSOrder received) 
    {
        logger.debug("Updating order "+id);

        // PGSOrder existing = getById(id);
        // Load existing order IN THIS TRANSACTION (not the read-only one)
        List<PGSOrder> orders = orderRepository.findByIdWithItems(id);
        if(orders.isEmpty()) throw new BadRequestException("Not found");
        PGSOrder existing = orders.get(0);
        logger.debug("Existing order loaded");
        
        existing.setStatus(received.getStatus());
        existing.setComment(received.getComment());
        existing.setPaymentMethod(received.getPaymentMethod());
        existing.setPrice(received.getPrice());
        existing.setCode(received.getCode());
        existing.setItems(received.getItems());

        PGSOrder updated = orderRepository.save(existing);
        
        notificationService.createNotification(
                "Narudžba "+updated.getCode()+" je: "+updated.getStatus(),"", "system", "*", "status_update");
        
        return updated;
    }    
    
    public OrderDTO update(Long id, OrderDTO dto) 
    {
        return OrderMapper.toDto( this.update(id, OrderMapper.toEntity(dto)));        
    }

    @Transactional
    public void updateOrderStatus(Long id, String status) 
    {
        int rowsAffected = orderRepository.updateOrderStatus(id, status);

        PGSOrder order = orderRepository.getReferenceById(id);
        
        if (rowsAffected == 0) 
        {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Order with id " + id + " not found"
            );
        }
        
        //TODO: Izgleda da nestampa ovo kad je na info
        logger.info("Order {}, id[{}] status updated to {}", order.getCode(), id, status);
        
        String msg = "";
        String type = "0";

        if(status.equals("READY"))
        {
            msg = notificationService.createOrderReadyText(order);
            type = "3";
            order.setSpremnoAt(LocalDateTime.now());
        }
        else if(status.equals("IN_PREPARATION"))
        {
            msg = notificationService.createOrderInprepText(order);
            type = "2";
            order.setUPripremiAt(LocalDateTime.now());
        }
        else if(status.equals("SERVED"))
        {
            msg = notificationService.createOrderServedText(order);
            type = "3";
            order.setServedAt(LocalDateTime.now());
        }
        else 
        {
            msg = notificationService.createOrderStatusText(order);
        }

        notificationService.createNotification(
            "", msg, order.getUser().getUsername()+",kitchen,admin", "*", type
        );
    }
}