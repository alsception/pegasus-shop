package org.alsception.pegasus.features.order;

import java.math.BigDecimal;
import java.util.List;
import org.alsception.pegasus.features.products.ProductRepository;
import org.alsception.pegasus.core.exception.BadRequestException;
import org.alsception.pegasus.core.utils.UniqueIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService 
{
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) 
    {
        this.orderRepository = orderRepository;
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
            orders = orderRepository.findByUsername(username);
        }
        else
        {
            orders = orderRepository.findByUsernameAndCode(username,"%"+search.replace("-", "").toUpperCase()+"%");   //Need to remove dashes from code. And also make it case sens
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
                    order.setCode(UniqueIdGenerator.toAlpha(order.getId()));
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
    
    @Transactional(readOnly = true)
    public PGSOrder getById(Long id) throws BadRequestException
    {
        List<PGSOrder> orders = orderRepository.findByIdWithItems(id);
        if(orders.isEmpty()) throw new BadRequestException("Not found");
        return orders.get(0);
    }
    
    public void delete(Long id)
    {
        this.orderRepository.deleteById(id);
    }    
    
}