package org.alsception.pegasus.core.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.users.UserRepository;
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
        logger.trace("Trying to find user: " + username);

        Optional<PGSUser> user = repository.findByUsername(username);

        if (user.isEmpty()) 
        {
            logger.warn("User not found in DB: " + username);
        } 
        else 
        {
            logger.trace("Found user in DB: " + user.get().getUsername());            
        }
        
        // Map your User entity to Spring's UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.get().getUsername())
                .password(user.get().getPassword()) // should already be bcrypt encoded!
                .roles(user.get().getRole().toString())
                .build();
    }
    
    public boolean userExists(String username)
    {
        logger.trace("Looking if username exists: " + username);

        Optional<PGSUser> user = repository.findByUsername(username);

        if (user.isEmpty()) 
        {
            logger.trace("Username is free for registration. | " + username);
            return false;
        } 
        else 
        {
            logger.trace("Username already present in DB: " + user.get().getUsername());            
            return true;
        }
        
    }
}
