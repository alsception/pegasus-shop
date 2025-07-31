package org.alsception.pegasus.features.cart;

import java.math.BigDecimal;
import org.alsception.pegasus.features.order.PGSOrder;
import java.util.List;
import java.util.stream.Collectors;
import org.alsception.pegasus.features.products.PGSProduct;
import org.alsception.pegasus.features.products.ProductRepository;
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.users.UserRepository;
import org.alsception.pegasus.core.utils.UniqueIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.alsception.pegasus.features.order.OrderRepository;
import org.alsception.pegasus.features.order.PGSCheckoutRequestDTO;
import org.alsception.pegasus.features.order.PGSOrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

@Service
public class CartService 
{
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    public CartService(UserRepository userRepository,
                       ProductRepository productRepository,
                       CartRepository cartRepository,
                       OrderRepository orderRepository) 
    {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }       
    
    @Transactional
    public void addProductToCart(Long userId, Long productId) 
    {
        this.addProductToCart(userId, productId, 1);
    }
    
    @Transactional
    public void addProductToCart(Long userId, Long productId, Integer quantity) 
    {
        PGSUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: "+userId));
        
        this.addProductToCart(user, productId, quantity);
    }

    @Transactional
    public void addProductToCart(String username, Long productId, Integer quantity) 
    {
        PGSUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: "+username));
        
        this.addProductToCart(user, productId, quantity);
    }
    
    @Transactional
    public void addProductToCart(String username, Long productId) 
    {
        PGSUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: "+username));
        
        this.addProductToCart(user, productId, 1);
    }
    
    @Transactional
    public void addProductToCart(PGSUser user, Long productId, Integer quantity) 
    {
        // 1. User check
        
        if(user == null)
        {
            throw new RuntimeException("User is null");
        }

        if (!user.isActive()) 
        {
            throw new RuntimeException("User is not active");
        }

        // 2. Product check
        PGSProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if(quantity == null)
            quantity = 1;
        
        if(quantity < 0)
            throw new RuntimeException("Illegal quantity: ["+quantity+"]. Must be > 0");

        if (!product.isActive() || product.getStockQuantity()<= 0 || (product.getStockQuantity() - quantity < 0)) 
        {
            throw new RuntimeException("Product is not available or out of stock");
        }

        // 3. Get or create cart
        PGSCart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    PGSCart newPGSCart = new PGSCart();
                    newPGSCart.setUser(user);
                    return cartRepository.save(newPGSCart);
                });

        // 4. Add product to cart
        PGSCartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) 
        {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(product.getBasePrice());//WHAT IF PRICE CHANGED???
        }
        else 
        {
            PGSCartItem newItem = new PGSCartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getBasePrice());
            cart.getItems().add(newItem);
        }

        // 5. Finally, save
        cartRepository.save(cart);
    }
    
    /**
     * This method will create new cart if cart doesnt exist
     *
     */    
    public PGSCart getCartByUserId(Long userId) 
    {
        PGSUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    PGSCart newPGSCart = new PGSCart();
                    newPGSCart.setUser(user);
                    return cartRepository.save(newPGSCart);
                });
        
    }
    
    /**
     * Same as other method
     *
     */    
    public PGSCart getCartByUsername(String username) 
    {
        log.debug("Searching username: "+username);
        
        PGSUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.debug("Looking for cart...");
        
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    PGSCart newPGSCart = new PGSCart();
                    newPGSCart.setUser(user);
                    return cartRepository.save(newPGSCart);
                });
    }
    
    public List<PGSCart> getAll(String search) 
    {
        List<PGSUser> user = userRepository.findByUsernameContaining(search);

        return cartRepository.findAll();              
    }
    
    @Async
    public void deleteCartAsync(PGSCart cart) 
    {
        cartRepository.deleteById(cart.getId()); // avoid LazyInitializationException
        log.trace("Cart cleared");
    }
    
    /*
     * Maybe pass the user object instead of string username ? -.-    
     */
    @Transactional
    public void checkout(PGSCheckoutRequestDTO prc, String username)
    {
        log.trace("Creating order");
        //1. Create order
        PGSOrder order = new PGSOrder();
        long id = UniqueIdGenerator.generateNanoId();
        order.setId(id);
        order.setCode(UniqueIdGenerator.toAlpha(id));
        
        log.trace("Assigning user");
        //2. Assign user
        PGSUser u = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: "+username));
        
        order.setUser(u);
        
        log.trace("Setting address");
        //3. Assign a) Address ...
        
        order.setAddress(prc.getAddress());
        order.setEmail(prc.getEmail());
        order.setName(prc.getName());
        order.setComment(prc.getComment());
        
        log.trace("and payment info");
        // ... and b) PaymentInfo
        order.setPaymentMethod(prc.getPaymentMethod());
        
                
        log.trace("Loading cart from db");
        // 4. Load cart from db
        PGSCart cart = this.getCartByUsername(username);         
        
        // 5. Assign cart items to order and calculate total price      
        BigDecimal totalPrice = BigDecimal.ZERO;
        log.trace("Setting cart items to order");
        List<PGSOrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> {
                PGSOrderItem orderItem = new PGSOrderItem();
                log.trace("Adding product: "+cartItem.getProduct().getCode());
                log.trace(cartItem.toString());
                
                /*****************************************************************
                 *  TODO: OVDE CEMO MORATI DA VIDIMO PRVO PRE CHECKOUTA 
                 * DALI JE PROIZVOD UOPSTE DOSTUPAN, ISTO KAO ADD TO CART...
                 * 
                 * 
                 * * A gde ti je product details page????
                 *
                 ****************************************************************/                
                orderItem.setOrder(order); // važno zbog @ManyToOne
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                log.trace("234");
                totalPrice.add(cartItem.getPrice());
                log.trace("238");
                return orderItem;
            })
            .collect(Collectors.toList());
        order.setItems(orderItems);
        
        // 6. ... dont forget the price
        order.setPrice(totalPrice);
        order.setCurrency("EUR");
        
        //7. Set initial status
        order.setStatus("CREATED");
        
        log.trace("Saving order to db");
        //8. Finally, save
        this.orderRepository.save(order);
        
        //9. And dont forget to clear cart.
        // we dont want to wait for this, it will run in background
        
        log.trace("Order created, deleting cart...");
        deleteCartAsync(cart); 
    }
    
}
