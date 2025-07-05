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
    
    @Value("${server.port:/}")
    private String port;
    
    private static final Logger logger = LoggerFactory.getLogger(PegasusApplication.class);

    public static void main(String[] args) 
    {
        // Manually set default profile if none is set
        System.setProperty("spring.profiles.active", "local");//Do we need this now?

        SpringApplication.run(PegasusApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) 
    {
       logger.info("Application context path is: " + contextPath + ", on port: "+port);
       logger.info("<================================================================================================>\n");
    }

}
