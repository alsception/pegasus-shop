package org.alsception.pegasus.core.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.alsception.pegasus.features.products.PGSProduct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.alsception.pegasus.features.products.ProductService;
import org.alsception.pegasus.features.users.UserDTO;
import org.alsception.pegasus.features.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataInitializer implements CommandLineRunner 
{
    private final ProductService productService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private String productsImport =
            """
            Coca-Cola limenka 0,33 l
            €2.50
            
            Coca-Cola Zero limenka 0,33 l
            €2.50
            
            Fanta Orange limenka 0,33 l
            €2.50
            Osvježavajuće bezalkoholno gazirano piće sa sokom od naranče.
            
            Pepsi limenka 0,33 l
            €2.50
            
            Pipi limenka 0,33
            €2.50
            
            Cedevita limun 0,34 l
            €2.50
            
            Cedevita naranča 0,34 l
            €2.50
            
            Jana ledeni čaj šumsko voće 0,5 l
            €2.50
            
            Jana ledeni čaj breskva 0,5 l
            €2.50
            
            Jana 0,5 l
            €2.00
            
            Jamnica 0,5 l
            €2.00
            
            Vina Laguna Malvazija 0,187 l
            €4.50
            12% vol
            
            Vina Laguna Merlot 0,187 l
            €4.50
            13% vol
            
            Kalrovačko limenka 0,5 l
            €3.00
            5% vol
            
            Heineken limenka 0,5 l
            €3.50
            5% vol
            
            Zlatni medvjed 0,5 l
            €4.20
            5% vol
            
            San Servolo pšenično pivo 0,5 l
            €7.00
            5% vol
            
            San Servolo Gold 0,75 l
            €11.00
            5% vol
            """;

    public DataInitializer(ProductService productService, UserService userService) 
    {
        this.productService = productService;
        this.userService = userService;
    }

    //This is executed every time application starts
    
    @Override
    public void run(String... args) 
    {        
        createDefaultUser();
               
        
        //List<PGSProduct> lp = this.parseProducts2(productsImport);
        
        //try{
            //lp.forEach(p -> this.productService.createProduct(p));
        //}catch(Exception e){
        //    logger.error(e.getMessage());
        //}
        
    }

    private void createDefaultUser()
    {
        logger.info("Creating default user");   
        //TODO: Only if user does not exists or there is no users, this should be created.
        //TODO: also move this password somewhere

        String u = "pgsadmin";
        String p = new StringBuilder(u).reverse().toString();
        UserDTO user = new UserDTO(u,p,"admin", true);
        
        try 
        {            
            this.userService.createUser(user);
            logger.info("Default user created");
        } 
        catch (Exception e) 
        {
            logger.warn("Default user not created (probably already exists)");
            logger.debug(e.getMessage());
        }
    }
        
    public List<PGSProduct> parseProducts2(String input) 
    {
        List<PGSProduct> products = new ArrayList<>();
        Scanner scanner = new Scanner(input);

        String currentName = null;
        BigDecimal currentPrice = null;
        StringBuilder currentDesc = new StringBuilder();
        int i = 0;
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("€")) {
                // Pronađena cijena - očisti znak i spremi
                String priceVal = line.replace("€", "").trim();
                currentPrice = new BigDecimal(priceVal);
                
                // Nakon što nađemo cijenu, provjeravamo sljedeću liniju za opis
                if (scanner.hasNextLine()) {
                    String possibleDesc = scanner.nextLine().trim();
                    // Ako sljedeća linija NE počinje s €, smatramo je opisom
                    if (!possibleDesc.isEmpty() && !possibleDesc.startsWith("€")) {
                        currentDesc.append(possibleDesc);
                    } else {
                        // Ako je prazna ili je nova cijena, moramo se vratiti (ovo je rijedak slučaj)
                        // ali za tvoj format, opis ide odmah ispod cijene
                    }
                }

                // Kreiraj objekt (ovdje imamo Name, Price i opcionalni Desc)
                if (currentName != null && currentPrice != null) {
                    System.out.println("name:"+currentName);
                    System.out.println("desc:"+currentDesc);
                    System.out.println("price:"+currentPrice);
                    System.out.println("-------------------------------");
                    products.add(new PGSProduct(currentName, currentPrice, currentDesc.toString(),"W-20260325-"+(++i),5));
                }

                // Resetiraj privremene varijable za sljedeći krug
                currentName = null;
                currentPrice = null;
                currentDesc = new StringBuilder();
            } else {
                // Ako linija ne počinje s €, a currentName je prazan, to je ime
                if (currentName == null) {
                    currentName = line;
                }
            }
        }
        scanner.close();
        return products;
    }
}