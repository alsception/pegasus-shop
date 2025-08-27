package org.alsception.pegasus.features.cart;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.cart.CartService;
import org.alsception.pegasus.features.users.UserService;
import org.alsception.pegasus.features.order.PGSCheckoutRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addProductToCart(
            Principal principal,
            @RequestParam Long productId,
            @RequestParam(required = false) Integer quantity) 
    {
        String username = principal.getName();
        logger.debug("Adding to cart for user: " + username);

        cartService.addProductToCart(username, productId, quantity);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Product added to cart");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String,Object>> updateProductQuantity(
            Principal principal,
            @RequestParam Long productId,
            @RequestParam Integer quantity)
    {
        String username = principal.getName();
        logger.debug("Updating quantity for product " + productId + " in cart for user: " + username);
        Map<String, Object> response = new HashMap<>();
        
        PGSCart cart = cartService.updateProductQuantity(username, productId, quantity);
        
        //Sort the cart items so they maintain the same order
        cart.getItems().sort((a, b) -> a.getCreated().compareTo(b.getCreated()));

        response.put("message", "Cart updated");
        response.put("cart", cart);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteProductFromCart(
            Principal principal,
            @RequestParam Long productId)
    {
        String username = principal.getName();
        logger.debug("Deleting product " + productId + " from cart for user: " + username);

        cartService.deleteProductFromCart(username, productId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Product removed from cart");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> checkout(
            Principal principal,//Spring autoinjects
            @RequestBody PGSCheckoutRequestDTO request) 
    {
        String username = principal.getName();

        logger.debug("Initiating checkout for user: "+ username);
        
        //todo:// Ovde bi se mogla dodati validacija tokena
        /**
         // Primer provere tokena (dummy provera)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        * }
        String token = authHeader.substring(7); // ukloni "Bearer "
        */    

        cartService.checkout(request, username);
        
        String msg = "Checkout successfull";
        logger.debug(msg + " ["+username+"]");
        
        Map<String, String> response = new HashMap<>();
        response.put("message", msg);
        
        //TODO: Actually return cart object together with message
        return ResponseEntity.ok(response);
    }       

    //TODO: FIXX ERROR MESSAGE AND METHOD WHEN 
    //[org.springframework.web.bind.MissingServletRequestParameterException: Required request parameter 'userId' for method parameter type Long is not present]
    @GetMapping
    public ResponseEntity<PGSCart> getCartByUser(Principal principal) 
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        logger.debug("Get cart for user: " + username);
        
        PGSCart cart = cartService.getCartByUsername(username);
        
        return ResponseEntity.ok(cart);
    }
    
//    @GetMapping("/all")
//    public ResponseEntity<PGSCart> getAll(Principal principal, @RequestParam(required = false) String search) //Maybe also add min quantity or other filters.
//    {
//        //Get all carts, only if user is admin
//        
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//        
//        Optional<User userService.findByUsername(username);
//
//        logger.debug("logged user is: " + username);
//        
//        PGSCart cart = cartService.getCarts(username);
//        
//        return ResponseEntity.ok(cart);
//    }tre
}
