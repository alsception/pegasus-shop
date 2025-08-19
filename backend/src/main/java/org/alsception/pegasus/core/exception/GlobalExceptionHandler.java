package org.alsception.pegasus.core.exception; 

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.BadRequestException;//TODO: try using our exception type
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotWritableException;

@ControllerAdvice
public class GlobalExceptionHandler 
{
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<Map<String, Object>> handleProductValidationException(ProductValidationException ex) 
    {
        logger.error(ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Product validation error");
        response.put("message",  ex.getMessage());
        response.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotWritableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonWriteError(HttpMessageNotWritableException ex) 
    {
        logger.error(ex.getMessage());        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", "Unable to serialize the response object.");
        response.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Optional: catch LazyInitializationException separately for logging
    @ExceptionHandler(org.hibernate.LazyInitializationException.class)
    public ResponseEntity<Map<String, Object>> handleLazyInitError(Exception ex) 
    {
        logger.error(ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "A lazy-loading error occurred");
        response.put("message", ex.getMessage());
        response.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Map<String, Object>> handleConversionError(HttpMessageConversionException ex) 
    {
        logger.error(ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Message conversion failed");
        response.put("message", ex.getMessage());
        response.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }    
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) 
    {
        logger.error(ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "BadRequest");
        response.put("message", ex.getMessage());
        response.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Any exception that is not specifically handled, will be handled by this method.
     * Also, wrong parameters types will be treated as bad request exception
     */
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) 
    {
        logger.error(ex.getMessage());
        Map<String, Object> response = new HashMap<>();

        if(ex.getMessage().contains("Failed to convert value"))
        {
            response.put("error", "BAD_REQUEST");
            response.put("message", ex.getMessage());
            response.put("timestamp", Instant.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        else
        {
            response.put("error", "ServerError");
            response.put("message", ex.getMessage());
            response.put("timestamp", Instant.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}