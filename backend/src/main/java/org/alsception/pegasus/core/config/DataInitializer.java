package org.alsception.pegasus.core.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.alsception.pegasus.features.products.ProductService;
import org.alsception.pegasus.features.table.PGSTableService;
import org.alsception.pegasus.features.users.UserDTO;
import org.alsception.pegasus.features.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataInitializer implements CommandLineRunner 
{
    private final ProductService productService;
    private final PGSTableService tableService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(ProductService productService, UserService userService,
        PGSTableService tableService) 
    {
        this.productService = productService;
        this.userService = userService;
        this.tableService = tableService;
    }

    //This is executed every time application starts
    
    @Override
    public void run(String... args) 
    {        
        createDefaultUser();

        //TODO: create million products test.

        //productService.generateSampleProducts(10);
        tableService.generateSampleTables();
    }

    private void createDefaultUser()
    {
        logger.info("Creating default user");   
        //TODO: Only if user does not exists or there is no users, this should be created.
        //TODO: also move this password somewhere

        String u = "pgsadmin";
        String p = "******";
        UserDTO user = new UserDTO(u,p,"admin", true);
        
        try 
        {            
            this.userService.createUser(user);
            logger.info("Default user created");
        } 
        catch (Exception e) 
        {
            logger.error("Error creating default user");
            logger.error(e.getMessage());
        }
    }
}