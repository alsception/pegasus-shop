package org.alsception.pegasus.core.users;

import java.net.URI;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController 
{
    @Autowired
    private UserService userService;
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @GetMapping
    public List<UserDTO> getUsers(
            @RequestParam(required = false) String search)
    {       
        //TODO: implement missing - sta missing?
        return userService.find(search);
    }  
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) 
    {
        UserDTO user = userService.findById(id);
        
        if (user != null) 
        {
            return ResponseEntity.ok(user);
        } 
        else 
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) throws BadRequestException
    {
        logger.debug("Create user request");
        logger.debug(user.toString());
        
        if(user.getUsername() == null || "".equals(user.getUsername()))
        {
            throw new BadRequestException("Username is mandatory");
        }
        if(user.getPassword() == null || "".equals(user.getPassword()))
        {
            throw new BadRequestException("Password cannot be null or empty");
        }
        
        UserDTO created = userService.createUser(user);
        URI location = URI.create("/users/" + created.getId());
        //location doesnt work
        return ResponseEntity
            .created(location)
            .body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO user) throws BadRequestException 
    {
        if (!id.equals(user.getId())) 
        {
            throw new BadRequestException("Path ID and body ID do not match.");
        }
        else if (!userService.existsById(id)) 
        { 
            return ResponseEntity.notFound().build();
        }
                
        user = userService.update(user);    
        
        return ResponseEntity.ok(user);            
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) 
    {
        if (userService.existsById(id)) 
        {        
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
        
    }
}
