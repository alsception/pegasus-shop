package org.alsception.pegasus.core.utils2;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 Are we actually use this somewhere? 
 */


public class BasicResponse 
{
        @JsonInclude(JsonInclude.Include.NON_NULL) 
	private String message;
        
        //Message code for eventual localization
        @JsonInclude(JsonInclude.Include.NON_NULL) 
        private String code; 

	public BasicResponse() {}	

	public BasicResponse(String message) {
            this.setMessage(message);
	}
        
        public BasicResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

    
        
        
}
