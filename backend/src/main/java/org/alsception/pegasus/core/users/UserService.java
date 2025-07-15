package org.alsception.pegasus.core.users;

import java.util.List;
import java.util.stream.Collectors;
import org.alsception.pegasus.core.utils.UniqueIdGenerator;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService 
{
    private final UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;    
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDTO createUser(UserDTO userDTO) throws BadRequestException 
    {
        // Convert DTO to Entity
        PGSUser user = new PGSUser();
        user.setId(UniqueIdGenerator.generateNanoId());
        user.setUsername(userDTO.getUsername());       
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setActive(userDTO.getActive());
        
        try 
        {
            user.setRole(PGSUserRole.valueOf(userDTO.getRole().toUpperCase()));
        } 
        catch (IllegalArgumentException | NullPointerException ex) 
        {
            throw new BadRequestException("Invalid role: " + userDTO.getRole());
        }
        
        // Save the user and convert it back to DTO
        PGSUser savedUser = repository.save(user);
        return new UserDTO(savedUser);  // Convert the saved entity to DTO
    }
    
    public PGSUser saveUser(PGSUser user, boolean encodePassword) 
    {
        user.setId(UniqueIdGenerator.generateNanoId());//Important
        if(encodePassword)
        {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setRole(PGSUserRole.CUSTOMER);//Later we will need to create also other user types
        user.setActive(Boolean.TRUE);
        
        PGSUser savedUser = repository.save(user);
        
        logger.info("User created: "+user.getUsername());
        return savedUser; 
    }
    
    
    public UserDTO update(UserDTO receivedUser, String requestorUsername) 
    {
        //first, see if requestor has rights to execute update
        // It needs to be ADMIN, or owner
        PGSUser requestor = findByUsername(requestorUsername);
        if( !(requestor.getRole().equals(PGSUserRole.ADMIN) || receivedUser.getUsername().equals(requestorUsername)))
        {
            throw new AccessDeniedException("User does not have permissions to perform this action");
        }

        //1.map
        //2.password
        //3.save
        
        PGSUser dbUser = repository.findById(receivedUser.getId()).get();
        
        //We can also do some checks, if there is a user with the same username
        //and different id -> then throws exception. but not now...
        
        dbUser.setUsername(receivedUser.getUsername());        
        dbUser.setFirstName(receivedUser.getFirstName());
        dbUser.setLastName(receivedUser.getLastName());
        dbUser.setActive(receivedUser.getActive());     
        dbUser.setEmail(receivedUser.getEmail());
        dbUser.setPhone(receivedUser.getPhone());
        dbUser.setOrganization(receivedUser.getOrganization());
        dbUser.setComment(receivedUser.getComment());        
        dbUser.setRole(PGSUserRole.valueOf(receivedUser.getRole()));
        dbUser.setDob(receivedUser.getDob());
        
        if(receivedUser.getPassword()!=null && !"".equals(receivedUser.getPassword()))
        {
            //assign new password only if received 
            dbUser.setPassword(passwordEncoder.encode(receivedUser.getPassword()));
        }        
        
        dbUser = repository.save(dbUser);
        
        logger.info("User updated: "+dbUser.getId());
        
        return new UserDTO(dbUser); 
    }

    public List<UserDTO> getAllUsers() 
    {
        return repository.findAll()
                         .stream()
                         .map(UserDTO::new) // Convert User -> UserDTO
                         .collect(Collectors.toList());
    }
    
    public List<UserDTO> find(String search) 
    {
        return repository.findByUsernameContaining(search)
                         .stream()
                         .map(UserDTO::new)
                         .collect(Collectors.toList());
    }
    
    /*public Optional<UserDTO> findByUsername(String username)
    {
        return repository.findByUsername(username)
                .map(UserDTO::new);
    }*/

    public PGSUser findByUsername(String username)
    {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserDTO findById(Long id) 
    {
        return repository.findById(id)
                         .map(UserDTO::new)
                         .orElse(null/*() -> new org.alsception.pegasus.core.exception.BadRequestException("User not found with id: " + id)*/);
    }
    
    public boolean existsById(Long id) 
    {
        return repository.existsById(id);
    } 
    
    public void deleteById(Long id, String requestorUsername) 
    {
        //See if requestor has rights to execute action
        //It needs to be ADMIN, or owner
        PGSUser requestor = findByUsername(requestorUsername);
        
        if( !(requestor.getRole().equals(PGSUserRole.ADMIN) || requestor.getId().equals(id)))
        {
            throw new AccessDeniedException("User does not have permissions to perform this action");
        }
        repository.deleteById(id);
    }     
}