package org.alsception.pegasus.core.security;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.alsception.pegasus.core.config.PGSConfigService;
import org.alsception.pegasus.core.logging.PGSLoggingService;
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController 
{   
    /**
     * Jednog lepog dana, register i login logika treba da ide u poseban service
     * Nazalost, pitaj boga kako su beanovi postavljeni, dobiju se raznorazne greske,
     * sta god da se pokusa. Ko uspe da resi, svaka cast
     * Zbog toga su i loginEnabled, registrationEnabled ovde.
     * Neuspeo pokusaj refaktoringa :( 14/7/25
     */


    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final PGSConfigService configService;
    private final PGSLoggingService logService;


    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                          CustomUserDetailsService userDetailsService,
                          UserService userService, PGSConfigService configService,
                          PGSLoggingService logService) 
    {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;        
        this.userService = userService;
        this.configService = configService;
        this.logService = logService;
    }

    /**
     * @param userDTO
     * @return ResponseEntity
     */

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest userDTO, HttpServletRequest request) 
    {
        try 
        {   
            logger.info("Registering new user: " + userDTO.getUsername());    
            
            if(!configService.isRegistrationEnabled())
            {
                logger.error("Registration disabled");
                
                return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body( "Registration is currently disabled");
            }   

            // Check if empty request
            if (null == userDTO.getUsername() || null == userDTO.getPassword() || userDTO.getUsername().isBlank() || userDTO.getPassword().isBlank()) 
            {
                logger.error("Empty register request: " + userDTO.toString());
                
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body( "Username or password cannot be blank");
            }

            // Check if user already exists
            if (userDetailsService.userExists(userDTO.getUsername())) 
            {
                logger.error("Duplicate username: " + userDTO.getUsername());
                
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body( "Username already exists");
            }


            // Create and save user
            PGSUser user = new PGSUser(userDTO);    
            userService.saveUser(user,true);            

            //TODO: Why dont we sign in automaticaly after registration? Or send json instead of plain text?

            this.log(request, "registration", user.getUsername());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Registration successfull!");
        } 
        catch (Exception e) 
        {                   
            try {
                printErrDetails("Error registering user", userDTO, e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest userDTO, HttpServletRequest request) 
    {
        logger.debug("Authenticating user: " + userDTO.getUsername());        
        
        //E sad, sta ako je login disabled? treba nam log, ali svejedno nije uspeo da se uloguje?
        this.log(request, "login", userDTO.getUsername());        

        if(!configService.isLoginEnabled())
        {
            logger.error("Login is currently disabled");
            
            return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body( "Login is currently disabled");
        }    

        try 
        {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
        } 
        catch (AuthenticationException e) 
        {
            //TODO: We should not return disabled message is account is disabled, and user tried to login with wrong password
            
            logger.warn(e.getMessage()+"!");            
            return ResponseEntity
                    .status(401)
                    .body(e.getMessage());
        }        
        catch (Exception e) 
        {
            logger.error(e.getMessage());            
            return ResponseEntity
                    .status(500)
                    .body(e.getMessage());
        }

        logger.trace("Loading user: " + userDTO.getUsername());
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getUsername());
        String token = jwtUtils.generateJwtToken(userDetails);  
        Map<String, String> response = new HashMap<>();
        response.put("token", token);  

        return ResponseEntity.ok(response);
    }

    public void log(HttpServletRequest request, String action, String username)
    {
        try
        {
            // 1. Dobijanje IP adrese (uz proveru za proxy/load balancer)
            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            } else {
                ipAddress = ipAddress.split(",")[0];
            }

            // 2. Dobijanje User-Agenta
            String userAgent = request.getHeader("User-Agent");   

            logService.logAction(username, action, ipAddress, userAgent);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    public void printErrDetails(String title, AuthRequest userDTO, String msg) throws IOException 
    {
        if(title == null || title.isEmpty()) title = "ERROR";
        logger.error("+======================================================== "+title.toUpperCase()+" ================================================+");
        logger.error("[1/2] Request: "+userDTO.toString());
        logger.error("[2/2] " + msg);
        logger.error("+================================================================================================================================+");       
    }
}