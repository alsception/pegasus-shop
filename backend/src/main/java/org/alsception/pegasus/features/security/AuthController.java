package org.alsception.pegasus.features.security;

import java.util.HashMap;
import java.util.Map;
import org.alsception.pegasus.core.users.PGSUser;
import org.alsception.pegasus.core.users.PGSUserRole;
import org.alsception.pegasus.core.users.UserService;
import org.alsception.pegasus.core.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController 
{   
//    @Autowired    
//    private CustomUserDetailsService userService;
//    
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                          CustomUserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder,
                          UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest userDTO) 
    {
        try {
            // Check if user already exists
            if (userDetailsService.userExists(userDTO.getUsername())) 
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Username already exists"));
            }

            // Create and save user
            String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(encryptedPassword);
            PGSUser user = new PGSUser(userDTO);    
            user.setActive(Boolean.TRUE);
            
            // Save to repository
            userService.saveUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully!"));

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest userDTO) 
    {
        logger.debug("Authenticating user: " + userDTO.getUsername());

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
            );
        } 
        catch (Exception e) 
        {
            logger.error("Authentication failed: " + e.getMessage());
            
            return ResponseEntity
                    .status(401)
                    .body("Authentication failed: " + e.getMessage());
        }

        logger.debug("Loading user: " + userDTO.getUsername());
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getUsername());
        
        String token = jwtUtils.generateJwtToken(userDetails);        
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        
        return ResponseEntity.ok(response);
    }
    
}