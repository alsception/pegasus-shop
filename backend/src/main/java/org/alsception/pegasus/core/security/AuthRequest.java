package org.alsception.pegasus.core.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alsception.pegasus.features.users.PGSUser;

@Data //Lombook for getters and setters
@NoArgsConstructor
public class AuthRequest 
{
    private Long id;
    private String username;
    private String role;  

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;    

    public AuthRequest(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public AuthRequest(PGSUser user) 
    {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().toString();
    }
}
