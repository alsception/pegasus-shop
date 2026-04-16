package org.alsception.pegasus.features.cart;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.alsception.pegasus.features.order.PGSOrder;
import java.util.List;
import java.util.stream.Collectors;
import org.alsception.pegasus.features.products.PGSProduct;
import org.alsception.pegasus.features.products.ProductRepository;
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.users.UserRepository;
import org.alsception.pegasus.core.config.PGSConfigService;
import org.alsception.pegasus.core.exception.ProductValidationException;
import org.alsception.pegasus.core.utils.CodeGenerator;
import org.alsception.pegasus.features.notifications.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.alsception.pegasus.features.order.OrderRepository;
import org.alsception.pegasus.features.order.PGSCheckoutRequestDTO;
import org.alsception.pegasus.features.order.PGSDailySession;
import org.alsception.pegasus.features.order.PGSDailySessionService;
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
    private final NotificationService notificationService;
    private final PGSDailySessionService pgsSessionService;
    private final PGSConfigService configService;
    
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public CartService(UserRepository userRepository,
                       ProductRepository productRepository,
                       CartRepository cartRepository,
                       OrderRepository orderRepository,
                       NotificationService notificationService,
                       PGSDailySessionService pgsSessionService,
                       PGSConfigService configService)
    {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;        
        this.pgsSessionService = pgsSessionService;
        this.configService = configService;
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
    public int addProductToCart(String username, Long productId, Integer quantity) 
    {
        PGSUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found: "+username));
        
        return this.addProductToCart(user, productId, quantity);
    }
    
    @Transactional
    public void addProductToCart(String username, Long productId) 
    {
        PGSUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found: "+username));
        
        this.addProductToCart(user, productId, 1);
    }
    
    @Transactional
    public int addProductToCart(PGSUser user, Long productId, Integer quantity) 
    {
        // 0. Shopping availability check
        if( !this.configService.isShoppingEnabled() )
        {
            throw new ProductValidationException("Naručivanje trenutno nije moguće. Ordering is currently unavailable.");
        }

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
        
        if ( quantity == null )
            quantity = 1;
        
        if ( quantity < -1 )
            throw new RuntimeException("Illegal quantity: ["+quantity+"]. Must be > -1");
        /**
         * Šta se ovde dešava? Ok ako je quantity = -1, smanjićemo količinu za 1
         * Ali neke tamo veće, odnosno manje količine, sad da smanjujemo za 10 komada
         * To je previše komplikovano da razmišljamo o tome.
         */

        if ( !product.isActive() ) 
        {
            throw new ProductValidationException("Proizvod trenutno nije dostupan");
        }

        // 3. Get or create cart
        PGSCart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    PGSCart newPGSCart = new PGSCart();
                    newPGSCart.setUser(user);
                    return cartRepository.save(newPGSCart);
                });

        // 4. Get item or create new
        PGSCartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        int newQt = 0;
            
        if ( existingItem != null ) 
        {   
            // 1. cena
            this.setCartItemPrice(existingItem, product);

            //2. Kolicina
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            newQt = existingItem.getQuantity();
            
            //Ako je nula mora ga obrisemo ne treba nam
            if(existingItem.getQuantity() <= 0)
            {               
               cart.getItems().remove(existingItem);  
               newQt = 0;  
            }                 
        }
        else 
        {
            /* Ako item ne postoji i trazeno je da se smanji onda ne treba nista
             */
            if( quantity != -1 )
            {
                //NEPOSTOJI PRAVIMO NEW ITEM 
                PGSCartItem newItem = new PGSCartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                newQt = newItem.getQuantity();

                this.setCartItemPrice(newItem, product);
                cart.getItems().add(newItem);
            }
            else
            {
                return 0;
            }
        }

        // 5. Finally, save and return new quantity
        cartRepository.save(cart);
        return newQt;
    }

    private void setCartItemPrice(PGSCartItem item, PGSProduct product)
    {
        // Ako imamo popust:
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) != 0) 
        {
            item.setPrice(product.getDiscount());
            item.setDiscounted(true);
        }
        else
        {
            item.setPrice(product.getBasePrice());
        }    
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

    @Transactional
    public PGSCart updateProductQuantity(String username, Long productId, Integer quantity) 
    {
        PGSUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        PGSCart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + username));

        PGSCartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart: " + productId));

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Transactional
    public void deleteProductFromCart(String username, Long productId) 
    {
        PGSUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        PGSCart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + username));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    /**
     * Same as other method
     *
     */    
    public PGSCart getCartByUsername(String username) 
    {
        logger.debug("Searching username: "+username);
        
        PGSUser user = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        logger.debug("Looking for cart...");
        
        return cartRepository.findByUser(user)
            .map(cart -> {
                cart.getItems().sort((a, b) -> a.getCreated().compareTo(b.getCreated()));
                return cart;
            })
            .orElseGet(() -> {
                PGSCart newPGSCart = new PGSCart();
                newPGSCart.setUser(user);
                return cartRepository.save(newPGSCart);
            });
    }

    public BigDecimal getCartTotalByUsername(String username) 
    {
        logger.debug("Searching username: "+username);
        
        PGSUser user = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        logger.debug("Looking for cart...");
        
        PGSCart cart = cartRepository.findByUser(user)            
            .orElseGet(() -> {
                PGSCart newPGSCart = new PGSCart();
                newPGSCart.setUser(user);
                return cartRepository.save(newPGSCart);
            });

        return cart.getTotalPrice().setScale(2, RoundingMode.HALF_UP);
    }
    
    public List<PGSCart> getAll(String search) 
    {
        List<PGSUser> user = userRepository.findByUsernameContainingIgnoreCaseOrderByCreatedDesc(search);

        return cartRepository.findAll();              
    }
    
    @Async
    public void deleteCartAsync(PGSCart cart) 
    {
        cartRepository.deleteById(cart.getId()); // avoid LazyInitializationException
        logger.trace("Cart cleared");
    }

    @Transactional
    public void deleteCart(String username) 
    {
        cartRepository.delete( this.getCartByUsername(username) ); 
        logger.info("Cart cleared ["+username+"]");
    }  

    public void deleteCart(PGSCart cart) 
    {
        cartRepository.delete(cart); 
        logger.trace("Cart cleared");
    }    
    
    /*
     * Maybe pass the user object instead of string username ? -.-    
     */
    @Transactional
    public long checkout(PGSCheckoutRequestDTO prc, String username)
    {
        //OVDE MORAMO PROVERITI DALI JE PRODAVAONICA UOPSTE UKLJUCENA, Shopping availability check
        if( !this.configService.isShoppingEnabled() )
        {
            throw new ProductValidationException("Naručivanje trenutno nije moguće. Ordering is currently unavailable.");
        }
        //TODO: takodje moramo staviti limite, da se ucitavaju iz baze podataka:
        //    * 1. MAX_CART_ITEMS
        //    * 2. MAX_CART_AMOUNT


        //Zatim proveravamo dali je otvoren fiskalni dan
        PGSDailySession dailySession = this.pgsSessionService.getActiveSession();
        //Ako nije dobro ovaj ce da baci exception ILI otvori novi dan
        
        //1. Create order
        logger.trace("Creating order");
        PGSOrder order = new PGSOrder();
        
        //assign daily session
        order.setSession(dailySession);
               
        
        //assign id: TODO: ovo proveriti dali je dobro
        long id = CodeGenerator.generateNanoId();
        order.setId(id);

        //calculate suffix
        String suffix = "D";    //Dostavan
        if( prc.getTakeAway() ) suffix = "T"; //Za van, take away        

        order.setCode(CodeGenerator.generateOrderCode(suffix));        
        
        //2. Assign user
        logger.trace("Assigning user");
        PGSUser u = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found: "+username));
        
        order.setUser(u);        
        
        //3. Set phone and address ...
        order.setAddress(prc.getAddress());
        order.setPhone(prc.getPhone());
        order.setComment(prc.getComment());                
        
        // 4. Load cart from db
        logger.trace("Loading cart from db");
        PGSCart cart = this.getCartByUsername(username);         
        
        // 5. Assign cart items to order and calculate total price      
        BigDecimal totalPrice = BigDecimal.ZERO;
        logger.trace("Setting cart items to order");
        
        List<PGSOrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> 
            {
                PGSOrderItem orderItem = new PGSOrderItem();
                logger.trace("Adding product: "+cartItem.getProduct().getCode());
                logger.trace(cartItem.toString());
                                
                // Validacija dostupnosti proizvoda
                PGSProduct p = productRepository.getReferenceById(cartItem.getProduct().getId());                
                if( !p.isActive() )
                {
                    throw new ProductValidationException("Proizvod trenutno nije dostupan / product currently not available: ["+p.getName()+"].");
                }

                if(cartItem.getQuantity() < 0)
                {
                    throw new RuntimeException("Illegal quantity: "+cartItem.getQuantity());
                }

                orderItem.setOrder(order); // važno zbog @ManyToOne
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                totalPrice.add(cartItem.getPrice());
                return orderItem;
            })
            .collect(Collectors.toList());
        
        order.setItems(orderItems);
        
        // 6. ... dont forget the price        
        calculatePrice(order);
        if (order.getPrice().compareTo(BigDecimal.ZERO) <= 0) 
        {
            //Nikad se nezna
            throw new RuntimeException("Illegal price: "+order.getPrice());
        }
        order.setCurrency("EUR");

        order.setPaymentMethod(""+prc.getPaymentMethod());
        
        //7. Set initial status
        order.setStatus("WAITING");        
        
        //8. Finally, save
        logger.trace("Saving order to db");
        this.orderRepository.save(order);
        
        //9. And dont forget to clear cart.
        // we dont want to wait for this, it will run in background
        
        logger.trace("Order created, deleting cart...");
        deleteCartAsync(cart); 

        logger.info("**New order {}, {}, id[{}] ", order.getCode(), username, id);
       
        //10. finally notification
        notificationService.createNotification(
            "Nova narudžba", 
            notificationService.createNewOrderText(order), order.getUser().getUsername(), 
            "*",//order.getUser().getUsername()+",kitchen,admin", 
            "1"
            );

        return id;
    }

    void calculatePrice(PGSOrder order)
    {
        BigDecimal total = order.getItems().stream()
            .map(item -> 
            {
                BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                return price.multiply(BigDecimal.valueOf(item.getQuantity()));
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setPrice(total);
    }

    
}
