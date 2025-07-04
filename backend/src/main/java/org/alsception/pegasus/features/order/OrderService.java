package org.alsception.pegasus.features.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.alsception.pegasus.features.products.ProductRepository;
import org.alsception.pegasus.core.users.PGSUser;
import org.alsception.pegasus.core.users.UserRepository;
import org.alsception.pegasus.core.utils.UniqueIdGenerator;
import org.alsception.pegasus.features.order.PGSOrder;
import org.springframework.stereotype.Service;

@Service
public class OrderService 
{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public OrderService(UserRepository userRepository,
                       ProductRepository productRepository,
                       OrderRepository orderRepository) 
    {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }
    
    /**
     * TODO: Check that user is 
     * 1. owner
     * 2. or admin (or webshop employee)     *
     */
    
    public List<PGSOrder> getByUserId(Long userId) 
    {
        /*PGSUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));*/
        
        return calculatePrice(orderRepository.findByUserId(userId));
    }
    
    public List<PGSOrder> getByUsername(String username) 
    {
        /*userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));*/

        return calculatePrice(orderRepository.findByUsername(username));
    }
    
    /**
     *  Price should be calculated at the moment of order creation (checkout).
     *  This is just the temporary use for old orders missing
     */
    public List<PGSOrder> calculatePrice(List<PGSOrder> orders)
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
        List<PGSUser> user = userRepository.findByUsernameContaining(search);

        return orderRepository.findAll();              
    }
    
    public Optional<PGSOrder> getById(Long id) 
    {
        return orderRepository.findById(id);//.orElse(new List<PGSOrder>());              
    }
    
//    public void checkout(PGSOrder order)
//    {
//        this.orderRepository.save(order);
//    }    
    
}
