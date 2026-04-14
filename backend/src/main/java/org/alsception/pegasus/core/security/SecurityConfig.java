package org.alsception.pegasus.core.security;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public SecurityConfig(PasswordEncoder passwordEncoder,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService customUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostConstruct
    public void printProfile() {
        if (activeProfile == null || activeProfile.isEmpty()) {
            logger.warn("⚠️ No active profile set. Running with default settings.");
        } else {
            logger.info("Active Spring profile: {}", activeProfile);
        }
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",                //storefront
                    "/index.html",      //storefront    
                    "/backoffice",      //backoffice
                    "/backoffice/**",   //backoffice
                    "/static/**", 
                    "/assets/**", 
                    "/lottie/**",       //animations
                    "/sound/**",        //notifications
                    "/favicon.ico", 
                    "/*.png", 
                    "/*.js", 
                    "/*.css",
                    "/manifest.json",   // za PWA
                    "/sw.js",           // za PWA
                    "/*.webmanifest"    // za PWA
                ).permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/api/sync/**").permitAll()    //Za cloud sync 
                .requestMatchers("/api/payments/webhook").permitAll()   //stripe webhook
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() 
    {
        logger.info("Creating CorsConfigurationSource");
        
        CorsConfiguration configuration = new CorsConfiguration();

        // Dynamic CORS based on profile
        if ("production".equals(activeProfile)) 
        {
            //TODO: LOAD THIS FROM APP.PROPERTIES
            logger.info("Whitelisting server: https://your-frontend.com");
            
            configuration.setAllowedOrigins(Collections.singletonList("https://your-frontend.com"));
        } 
        else 
        {   
            List allowedServers = Arrays.asList(
                    "http://localhost:5173",                        //Local dev
                    "http://localhost:8080",                        //Local build
                    "https://localhost",                            //Local build
                    "http://192.168.1.70:8080",                     //Local network build
                    "http://192.168.1.70:5173",                     //Local network dev
                    "https://192.168.1.70",                         //Local network secure
                    "https://192.168.1.70:5173",                    //Local dev secure
                    "https://2799-5-39-135-139.ngrok-free.app",     //ngrok tunnel
                    "https://6bccb69e3fd5.ngrok-free.app",          //ngrok2
                    "https://pegasus-uvnn.onrender.com",            //Render public
                    "http://192.168.178.66",                        //Barbacoa network
                    "http://192.168.178.66:5173"                    ////Barbacoa network
            );
            
            configuration.setAllowedOrigins(allowedServers);
            
            logger.info("Whitelisted servers: " + allowedServers.toString());
        }
                
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        logger.trace("CORS details set: headers, methods, and credentials");
        
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}
