package org.alsception.pegasus.features.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.alsception.pegasus.core.users.PGSUser;
import org.alsception.pegasus.core.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {        
        logger.debug("Trying to find user: " + username);

        Optional<PGSUser> user = repository.findByUsername(username);

        if (user.isEmpty()) 
        {
            logger.warn("User not found in DB: " + username);
        } 
        else 
        {
            logger.debug("Found user in DB: " + user.get().getUsername());            
            logger.debug(user.toString());
        }
        
        // Map your User entity to Spring's UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.get().getUsername())
                .password(user.get().getPassword()) // should already be bcrypt encoded!
                .roles(user.get().getRole().toString()) // or authorities
                .build();
    }
    
    public boolean userExists(String username)
    {
        logger.debug("Looking if username exists: " + username);

        Optional<PGSUser> user = repository.findByUsername(username);

        if (user.isEmpty()) 
        {
            logger.debug("Username is free for registration. | " + username);
            return false;
        } 
        else 
        {
            logger.warn("Username already present in DB: " + user.get().getUsername());            
            return true;
        }
        
    }
}
