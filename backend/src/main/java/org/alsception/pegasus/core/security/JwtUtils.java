package org.alsception.pegasus.core.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Use the configured jwtSecret to derive the signing key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(UserDetails userDetails) 
    {
        // Use the expiration time from the config
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpirationMs);

        String token = Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
        
        logger.trace("created token: "+token);
        
        return token;
    }

    public boolean validateJwtToken(String token) 
    {
        try 
        {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Use the signing key derived from jwtSecret
                .build()
                .parseClaimsJws(token);  // This will throw an exception if the token is invalid
            return true;
        } 
        catch (JwtException | IllegalArgumentException e) 
        {
            logger.error("Invalid JWT: " + e.getMessage());
        }
        return false;
    }

    public String getUsernameFromJwtToken(String token) 
    {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}