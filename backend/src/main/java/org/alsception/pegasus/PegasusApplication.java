package org.alsception.pegasus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class PegasusApplication implements ApplicationListener<ApplicationReadyEvent>
{    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    private static final Logger logger = LoggerFactory.getLogger(PegasusApplication.class);

    public static void main(String[] args) 
    {
        // Manually set default profile if none is set
        System.setProperty("spring.profiles.active", "local");

        SpringApplication.run(PegasusApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) 
    {
       logger.info("Application context path is: " + contextPath);
       logger.info("<================================================================================================>\n");
    }

}
