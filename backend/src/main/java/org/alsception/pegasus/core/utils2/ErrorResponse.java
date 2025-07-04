package org.alsception.pegasus.core.utils2;

import com.fasterxml.jackson.annotation.JsonInclude;

//U slučaju greške api će vraćati objekte ovog tipa.

/**
 Are we actually use this somewhere? 
 */

public class ErrorResponse {
    
        @JsonInclude(JsonInclude.Include.ALWAYS) 
        private final boolean error = true;
	    
        @JsonInclude(JsonInclude.Include.NON_NULL) 
	private String message;

	public ErrorResponse() {}	

	public ErrorResponse(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
        
        public boolean isError() {
            return error;
        }
}
