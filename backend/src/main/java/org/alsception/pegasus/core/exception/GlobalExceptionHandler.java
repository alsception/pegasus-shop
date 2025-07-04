package org.alsception.pegasus.core.exception;  // Change to your actual package

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
    public ResponseEntity<Map<String, String>> handleProductValidationException(ProductValidationException ex) 
    {
        logger.debug("Handling ProductValidationException");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotWritableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonWriteError(HttpMessageNotWritableException ex) 
    {
        logger.debug("Handling HttpMessageNotWritableException");
        
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", 500);
        errorBody.put("error", "Internal Server Error");
        errorBody.put("message", "Unable to serialize the response object.");
        errorBody.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }

    // Optional: catch LazyInitializationException separately for logging
    @ExceptionHandler(org.hibernate.LazyInitializationException.class)
    public ResponseEntity<String> handleLazyInitError(Exception ex) 
    {
        logger.debug("Handling LazyInitializationException");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("A lazy-loading error occurred.");
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Map<String, Object>> handleConversionError(HttpMessageConversionException ex) 
    {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("error", "Message conversion failed");
        error.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }    
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) 
    {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("error", "BadRequest");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Any exception that is not specifically handled, will be handled by this method
     */
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) 
    {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("error", "ServerError");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
