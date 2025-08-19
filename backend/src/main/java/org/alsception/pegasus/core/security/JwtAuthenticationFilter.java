package org.alsception.pegasus.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException 
    {
        logger.trace("Invoking jwt filter");

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) 
        {
            logger.trace("Found authHeader: {}***", authHeader.substring(0, 10));
            
            String token = authHeader.substring(7);
            
            logger.trace("Found token: {}***", token.substring(0, 10));
            
            try 
            {
                if (jwtUtils.validateJwtToken(token)) 
                {
                    String username = jwtUtils.getUsernameFromJwtToken(token);
                    
                    logger.trace("Got username from token: "+username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } 
                else 
                {
                    // Token invalid -> reject the request 
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } 
            catch (Exception e) 
            {
                // Any unexpected error during token validation -> reject request
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        else
        {    
            if(shouldWePrint(request))       
                printRequestDetails(request);
        }

        filterChain.doFilter(request, response);
    }
    
    public void printRequestDetails(HttpServletRequest request) throws IOException 
    {
        /*
         * We need to create this message as one, because of multithreading
         */
        String msg = "\n"+"+======================== UNATHORIZED HTTP REQUEST DETAILS ======================================+\n"; 
        msg += "[1/2] No auth header | "+request.getMethod()+" "+request.getRequestURI()+"\n";

        final String serverInfo = "Server Info [" + request.getServerName() + ":" + request.getServerPort() + "]";
        final String remoteInfo = "Remote Info [" + request.getRemoteAddr() + ":" + request.getRemotePort() + "]";
        final String query = "Query String [" + request.getQueryString()+"]";            

        msg+= "[2/2] " + serverInfo + ", " + remoteInfo + ", " + query + "\n";
        msg+= "+================================================================================================+";
        logger.warn(msg);
    }

    /*
     * Filters out unimportant requests, such as requests to / (index page), and assets
     */
    public boolean shouldWePrint(HttpServletRequest request)
    {
        if( 
            (request.getMethod().equals("POST") 
            && (request.getRequestURI().equals("/api/auth/login") || request.getRequestURI().equals("/api/auth/register"))))
        {
            //Login and register requests are unathorized 
            return false;
        }
        else if ((request.getMethod().equals("GET") 
            && (request.getRequestURI().equals("/") || request.getRequestURI().startsWith("/assets/"))))
        {
            return false;
        }
        return true;
    }
}