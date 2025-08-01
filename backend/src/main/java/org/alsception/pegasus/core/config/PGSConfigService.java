package org.alsception.pegasus.core.config;

import java.util.Optional;

import org.alsception.pegasus.core.security.CustomUserDetailsService;
import org.alsception.pegasus.core.security.JwtUtils;
import org.alsception.pegasus.features.cart.CartRepository;
import org.alsception.pegasus.features.cart.CartService;
import org.alsception.pegasus.features.order.OrderRepository;
import org.alsception.pegasus.features.products.ProductRepository;
import org.alsception.pegasus.features.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PGSConfigService 
{
    private final PGSConfigRepository configRepository;
    private static final Logger logger = LoggerFactory.getLogger(PGSConfigService.class);

    public PGSConfigService( PGSConfigRepository configRepository) 
    {
        this.configRepository = configRepository;
    }

    public boolean isLoginEnabled()
    {
        /**
         * b_prefix means it is boolean
         */
        return this.isServiceEnabled("b_login_enabled");
    }

    public boolean isRegistrationEnabled()
    {
        return this.isServiceEnabled("b_registration_enabled");
    }

    public boolean isServiceEnabled(String parameterName)
    {
        try{
            logger.trace("Loading service config from database");
            Optional<PGSConfig> pgsc = this.configRepository.findByName(parameterName);
            if(pgsc.isPresent())
            {
                return pgsc.get().getBooleanvalue();
            }
        }
        catch(Exception e)
        {
            logger.error("Could not load configuration from database", e.getMessage());
        }  

        return true;
    }

}