package org.alsception.pegasus.features.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Lombook for getters and setters
@NoArgsConstructor
public class UserDTO 
{
    private Long id;
    private String username;
    private String role;    
    private LocalDateTime created;    
    private LocalDateTime modified;    
    private LocalDateTime dob;
    private String firstName;    
    private String lastName;    
    private String email;    
    private String phone;    
    private String organization;    
    private String comment;  
    private Boolean active;    

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;    

    public UserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserDTO(PGSUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().toString();
        this.created = user.getCreated();
        this.modified = user.getModified();
        this.dob = user.getDob();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.active = user.isActive();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.organization = user.getOrganization();
        this.comment = user.getComment();
    }

    public PGSUser convert(UserDTO user) 
    {
        PGSUser p = new PGSUser();
        p.setId(user.getId());
        p.setUsername(user.getUsername());
        p.setPassword(user.getPassword());
        p.setRole(PGSUserRole.valueOf(user.getRole().toString()));
        p.setCreated(user.getCreated());
        p.setModified(user.getModified());
        p.setDob(user.getDob());
        p.setFirstName(user.getFirstName());
        p.setLastName(user.getLastName());
        p.setActive(user.getActive());
        p.setPhone(user.getPhone());
        p.setEmail(user.getEmail());
        p.setOrganization(user.getOrganization());
        p.setComment(user.getComment());
        return p;
    }
}
