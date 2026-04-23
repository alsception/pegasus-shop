package org.alsception.pegasus.core.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
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
    
    //Paznja ovde ima duplih!!!
    private String productsImport =
            """
            Juneći ćevapi|https://imageproxy.wolt.com/menu/menu-images/shared/ff4468a2-bf56-11ee-be67-26adf0ac3b59_barbacoa___26.01.2024_product_87.jpg
            Juneći ćevapi|https://imageproxy.wolt.com/menu/menu-images/shared/ff4468a2-bf56-11ee-be67-26adf0ac3b59_barbacoa___26.01.2024_product_87.jpg
            Pileći file|https://imageproxy.wolt.com/menu/menu-images/shared/491a2916-bf58-11ee-8c14-7a613bb4c054_barbacoa___26.01.2024_product_138.jpg
            Iskošteni pileći batak/zabatak|https://imageproxy.wolt.com/menu/menu-images/shared/4dcaa558-bf58-11ee-b7c7-cece4721fefe_barbacoa___26.01.2024_product_130.jpg
            Juneći ćevapi sa sirom|https://imageproxy.wolt.com/menu/menu-images/shared/04c98ece-bf57-11ee-94d3-e62bf5a4cb0b_barbacoa___26.01.2024_product_89.jpg
            Šopska salata|https://imageproxy.wolt.com/menu/menu-images/shared/d37404d0-bf4c-11ee-ab18-6695feec1b8d_barbacoa___26.01.2024_product_57.jpg
            
            Mazalice s kajmakom|https://imageproxy.wolt.com/menu/menu-images/shared/1edff92a-bf56-11ee-a788-16872310c4f3_barbacoa___26.01.2024_product_1.jpg
            Zapečeni grah|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/dde75c14-bf6a-11ee-92b3-ca9b4b69182c_barbacoa___26.01.2024_product_75.jpg
            Ploške mladog krumpira|https://imageproxy.wolt.com/menu/menu-images/shared/2656e8e4-bf4c-11ee-9023-aebcd39973ba_barbacoa___26.01.2024_product_65.jpg
            Punjeni rolani pileći file|https://imageproxy.wolt.com/menu/menu-images/shared/3130298c-bf57-11ee-8289-baa69cf70b0d_barbacoa___26.01.2024_product_95.jpg
            Punjena vješalica|https://imageproxy.wolt.com/menu/menu-images/shared/373d550c-bf57-11ee-b8aa-76596533c331_barbacoa___26.01.2024_product_97.jpg
            Gurmanske mazalice s kajmakom|https://imageproxy.wolt.com/menu/menu-images/shared/20e94b36-bf56-11ee-b95f-16872310c4f3_barbacoa___26.01.2024_product_3.jpg
            Beef steak|https://imageproxy.wolt.com/menu/menu-images/shared/948ed062-bf68-11ee-9091-264f57d90258_barbacoa___26.01.2024_product_125.jpg
            Rib Eye steak|https://imageproxy.wolt.com/menu/menu-images/shared/91fb9d80-bf68-11ee-b1e9-de1845f3baa3_barbacoa___26.01.2024_product_127.jpg
            Punjena vješalica|https://imageproxy.wolt.com/menu/menu-images/shared/373d550c-bf57-11ee-b8aa-76596533c331_barbacoa___26.01.2024_product_97.jpg
            Punjeni rolani pileći file|https://imageproxy.wolt.com/menu/menu-images/shared/3130298c-bf57-11ee-8289-baa69cf70b0d_barbacoa___26.01.2024_product_95.jpg
            Pljeskavica Barbacoa - đevrek|https://imageproxy.wolt.com/menu/menu-images/shared/79d10d4a-bf67-11ee-b9f5-5a9972fb470a_barbacoa___26.01.2024_product_105.jpg
            Juneća gurmanska pljeskavica|https://imageproxy.wolt.com/menu/menu-images/shared/6a1aaaa0-bf67-11ee-8a1c-264f57d90258_barbacoa___26.01.2024_product_109.jpg
            Pileća gurmanska pljeskavica|https://imageproxy.wolt.com/menu/menu-images/shared/b3def606-bf57-11ee-a75b-d6b4827153d2_barbacoa___26.01.2024_product_113.jpg
            Juneći gurmanski uštipci|https://imageproxy.wolt.com/menu/menu-images/shared/282deee6-bf57-11ee-b350-7a613bb4c054_barbacoa___26.01.2024_product_93.jpg
            Pileći gurmanski uštipci|https://imageproxy.wolt.com/menu/menu-images/shared/1f2b9c48-bf58-11ee-9133-26adf0ac3b59_barbacoa___26.01.2024_product_151.jpg
            Dimljena vješalica|https://imageproxy.wolt.com/menu/menu-images/shared/fc3c4818-bf66-11ee-8106-e68ddc690a19_barbacoa___26.01.2024_product_115.jpg
            Juneći ćevapi sa sirom|https://imageproxy.wolt.com/menu/menu-images/shared/04c98ece-bf57-11ee-94d3-e62bf5a4cb0b_barbacoa___26.01.2024_product_89.jpg
            Pileći ćevapi XXL sa sirom|https://imageproxy.wolt.com/menu/menu-images/shared/0cd10bfa-bf58-11ee-a503-468ddb07a32d_barbacoa___26.01.2024_product_123.jpg
            Pileća pljeskavica sa sirom|https://imageproxy.wolt.com/menu/menu-images/shared/aef10ca6-bf57-11ee-8248-7a613bb4c054_barbacoa___26.01.2024_product_111.jpg
            Juneći gurmanski ćevapi|https://imageproxy.wolt.com/menu/menu-images/shared/f18d95e8-bf57-11ee-a5d5-2e1cf868d317_barbacoa___26.01.2024_product_121.jpg
            Juneća pljeskavica sa sirom|https://imageproxy.wolt.com/menu/menu-images/shared/4196ea2c-bf57-11ee-a8db-6a05a4a74639_barbacoa___26.01.2024_product_99.jpg
            Pileći file|https://imageproxy.wolt.com/menu/menu-images/shared/491a2916-bf58-11ee-8c14-7a613bb4c054_barbacoa___26.01.2024_product_138.jpg
            Juneća pljeskavica s lukom|https://imageproxy.wolt.com/menu/menu-images/shared/498f1cb8-bf57-11ee-bbb0-fa52aadeba5e_barbacoa___26.01.2024_product_101.jpg
            
            Pileći ražnjići|https://imageproxy.wolt.com/menu/menu-images/shared/6e69ad1e-bf4d-11ee-a86b-265d864ab0c4_barbacoa___26.01.2024_product_140.jpg
            Svinjski ražnjići|https://imageproxy.wolt.com/menu/menu-images/shared/5cbdd51c-bf58-11ee-a04e-06c785117791_barbacoa___26.01.2024_product_142.jpg
            Iskošteni pileći batak/zabatak|https://imageproxy.wolt.com/menu/menu-images/shared/4dcaa558-bf58-11ee-b7c7-cece4721fefe_barbacoa___26.01.2024_product_130.jpg
            Domaća dimljena kobasica|https://imageproxy.wolt.com/menu/menu-images/shared/daa6020c-bf57-11ee-815c-768576f3bbff_barbacoa___26.01.2024_product_119.jpg
            Domaća dimljena pileća kobasica|https://imageproxy.wolt.com/menu/menu-images/shared/d39a09c2-bf57-11ee-bd2c-c28bb31354a3_barbacoa___26.01.2024_product_117.jpg
            Gurmanske mazalice s kajmakom|https://imageproxy.wolt.com/menu/menu-images/shared/20e94b36-bf56-11ee-b95f-16872310c4f3_barbacoa___26.01.2024_product_3.jpg
            Mazalice s kajmakom|https://imageproxy.wolt.com/menu/menu-images/shared/1edff92a-bf56-11ee-a788-16872310c4f3_barbacoa___26.01.2024_product_1.jpg
            Plata Barbacoa Special za 4 osobe|https://imageproxy.wolt.com/menu/menu-images/shared/5a311538-bf68-11ee-a285-465eadb4cde1_barbacoa___26.01.2024_product_137.jpg
            Plata Barbacoa za 4 osobe|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/04936986-ca7c-11ee-a413-5a919cbd6ac9_barbacoa___26.01.2024_product_135__2_.jpg
            Plata Barbacoa Special za 2 osobe|https://imageproxy.wolt.com/menu/menu-images/shared/1a1fe842-bf69-11ee-aa63-dec79f558b86_barbacoa___26.01.2024_product_150.jpg
            Plata Barbacoa za 2 osobe|https://imageproxy.wolt.com/menu/menu-images/shared/4e8d0548-bf68-11ee-a273-e68ddc690a19_barbacoa___26.01.2024_product_144.jpg
            Plata Chicken za 2 osobe|https://imageproxy.wolt.com/menu/menu-images/shared/f70c463e-bf68-11ee-8a54-cedc14fdbb28_barbacoa___26.01.2024_product_92.jpg
            Šopska salata|https://imageproxy.wolt.com/menu/menu-images/shared/d37404d0-bf4c-11ee-ab18-6695feec1b8d_barbacoa___26.01.2024_product_57.jpg
            Mix lisnatih salata|https://imageproxy.wolt.com/menu/menu-images/shared/03bfe9b0-bf4d-11ee-abb0-b66c1e9147a1_barbacoa___26.01.2024_product_11.jpg
            Tarator salata|https://imageproxy.wolt.com/menu/menu-images/shared/8065d976-bf56-11ee-b642-663465340c5b_barbacoa___26.01.2024_product_27.jpg
            Poriluk salata|https://imageproxy.wolt.com/menu/menu-images/shared/da36b704-bf56-11ee-a37e-66f015f74d95_barbacoa___26.01.2024_product_59.jpg
            Vitaminska salata|https://imageproxy.wolt.com/menu/menu-images/shared/f6ff5f08-bf4c-11ee-a9af-229a5ddd1e85_barbacoa___26.01.2024_product_9.jpg
            Gurmanska salata|https://imageproxy.wolt.com/menu/menu-images/shared/a75b9fac-bf56-11ee-990c-c2854ce021e6_barbacoa___26.01.2024_product_47.jpg
            Miješana salata|https://imageproxy.wolt.com/menu/menu-images/shared/7154ed3c-bf56-11ee-be97-465aa8452e66_barbacoa___26.01.2024_product_25.jpg
            Pečena paprika|https://imageproxy.wolt.com/menu/menu-images/shared/c5d7fe80-bf4c-11ee-a5f8-a6c9703c959c_barbacoa___26.01.2024_product_5.jpg
            Ljuta papričica|https://imageproxy.wolt.com/menu/menu-images/shared/bd74c714-bf4c-11ee-b072-0ee4151b55de_barbacoa___26.01.2024_product_7.jpg
            Grillano povrće|https://imageproxy.wolt.com/menu/menu-images/shared/18b3999e-bf4c-11ee-be61-c27f8e407bf3_barbacoa___26.01.2024_product_69.jpg
            Grillani šampinjoni|https://imageproxy.wolt.com/menu/menu-images/shared/1d48722c-bf4c-11ee-a0d7-ce2dd4eba1c5_barbacoa___26.01.2024_product_77.jpg
            Zapečeni grah|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/dde75c14-bf6a-11ee-92b3-ca9b4b69182c_barbacoa___26.01.2024_product_75.jpg
            Pekarski krumpir|https://imageproxy.wolt.com/menu/menu-images/shared/227b7dca-bf4c-11ee-8634-06a5642543cd_barbacoa___26.01.2024_product_71.jpg
            Ploške mladog krumpira|https://imageproxy.wolt.com/menu/menu-images/shared/2656e8e4-bf4c-11ee-9023-aebcd39973ba_barbacoa___26.01.2024_product_65.jpg
            Pomfrit|https://imageproxy.wolt.com/menu/menu-images/shared/243508d4-bf4c-11ee-a4c8-4a1a81f69aed_barbacoa___26.01.2024_product_67.jpg
            Pommes frites|https://imageproxy.wolt.com/menu/menu-images/shared/243508d4-bf4c-11ee-a4c8-4a1a81f69aed_barbacoa___26.01.2024_product_67.jpg
            Kajmak|https://imageproxy.wolt.com/menu/menu-images/shared/3d8487f6-bf4c-11ee-957b-32ac2fbf9021_barbacoa___26.01.2024_product_37.jpg
            Urnebes|https://imageproxy.wolt.com/menu/menu-images/shared/3b7b51ec-bf4c-11ee-a39f-827b349bd79d_barbacoa___26.01.2024_product_40.jpg
            Lepinja|https://imageproxy.wolt.com/menu/menu-images/shared/0fe14fb4-bf4c-11ee-8b3b-56bcea55d5c9_barbacoa___26.01.2024_product_147.jpg
            Tartar|https://imageproxy.wolt.com/menu/menu-images/shared/62836bda-bf4c-11ee-959b-4a1a81f69aed_barbacoa___26.01.2024_product_41.jpg
            Majoneza|https://imageproxy.wolt.com/menu/menu-images/shared/c14a45da-bf56-11ee-a4f9-1265ef374638_barbacoa___26.01.2024_product_45.jpg
            Ajvar|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/7c6232aa-e2bc-11ee-9dea-eeb851ad191d_barbacoa___26.01.2024_product_63.jpg
            Ketchup|https://imageproxy.wolt.com/menu/menu-images/shared/c378e938-bf56-11ee-98cc-16872310c4f3_barbacoa___26.01.2024_product_33.jpg
            Senf|https://imageproxy.wolt.com/menu/menu-images/shared/e8c80f44-bf4b-11ee-a9d4-827b349bd79d_barbacoa___26.01.2024_product_35.jpg
            Mousse čokolada|https://imageproxy.wolt.com/menu/menu-images/shared/c7eda402-c048-11ee-aea0-b6050c2fa1be_barbacoa___26.01.2024_product_53.jpg
            Tiramisu|https://imageproxy.wolt.com/menu/menu-images/shared/c3eb06b0-c048-11ee-9d6f-dee338dee465_barbacoa___26.01.2024_product_55.jpg
            Krostata limun|https://imageproxy.wolt.com/menu/menu-images/shared/2ab10c64-c049-11ee-975b-223e97fd5313_barbacoa___26.01.2024_product_51.jpg
            Torta šumsko voće|https://imageproxy.wolt.com/menu/menu-images/shared/cd398520-c048-11ee-a345-9e776fe3e2a4_barbacoa___26.01.2024_product_49.jpg
            Palačinke Nutella sa orasima|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/282f2c1c-ca6e-11ee-bc26-fade2a5163c8_barbacoa___26.01.2024_product_85.jpg
            Palačinke Nutella|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/0acde406-ca6e-11ee-99ef-42972c7d0b2e_barbacoa___26.01.2024_product_83.jpg
            Palačinke orasi|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/f39b4710-ca6d-11ee-86a3-ba9dd2381fea_barbacoa___26.01.2024_product_81.jpg
            Palačinke marmelada|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/d28758d4-ca6d-11ee-b104-e69a94027456_barbacoa___26.01.2024_product_79.jpg
            Coca-Cola limenka 0,33 l|https://imageproxy.wolt.com/menu/menu-images/5c9cb82dbd41af000cc11520/b71212d0-770a-11ee-afd8-c6a1333dc619_5449000214911.png
            Coca-Cola Zero limenka 0,33 l|https://imageproxy.wolt.com/menu/menu-images/5c9cb82dbd41af000cc11520/b9c54d80-770a-11ee-aaf2-62eba8934976_5449000214799.png
            Fanta Orange limenka 0,33 l|https://imageproxy.wolt.com/menu/menu-images/5c9cb82dbd41af000cc11520/b9c63e16-770a-11ee-87fc-a22b885348c1_5449000011527.png
            Pepsi limenka 0,33 l|https://imageproxy.wolt.com/menu/menu-images/shared/9fe9ea8e-d717-11ee-8ce5-ea9f8f815dba_frame_55__1_.png
            Pipi limenka 0,33|https://imageproxy.wolt.com/menu/menu-images/65afb76a33b8ead5d25b3f51/6b5e0f6e-d7aa-11ee-95b4-2208b079c46c_frame_45.png
            Cedevita limun 0,34 l|https://imageproxy.wolt.com/menu/menu-images/shared/307c6ee2-d717-11ee-9113-66b2fb9005ab_frame_53.png
            Cedevita naranča 0,34 l|https://imageproxy.wolt.com/menu/menu-images/shared/54e872ee-d717-11ee-ab89-46265c203022_frame_54.png
            Jana ledeni čaj šumsko voće 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/bd2b4192-d717-11ee-b1e8-c2d231d02b4e_frame_32__1_.png
            Jana ledeni čaj breskva 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/bb7df326-d717-11ee-9316-96e8b1178f4e_frame_30__1_.png
            Jana 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/afde4804-d717-11ee-82e6-66b2fb9005ab_frame_44__7_.png
            Jamnica 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/b1b0803e-d717-11ee-9797-ea9f8f815dba_frame_31__6_.png
            Vina Laguna Malvazija 0,187 l|https://imageproxy.wolt.com/menu/menu-images/shared/15d9e19a-d718-11ee-95b0-e2c9732e04c9_frame_40.png
            Vina Laguna Merlot 0,187 l|https://imageproxy.wolt.com/menu/menu-images/shared/39cc18ac-d718-11ee-9d95-96e8b1178f4e_frame_41__1_.png
            Kalrovačko limenka 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/fa2b4d0c-d718-11ee-afcf-0e3219b70bad_frame_33__1_.png
            Heineken limenka 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/f96151f0-d718-11ee-90eb-0e3219b70bad_frame_6__2_.png
            Zlatni medvjed 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/1cf35de8-d719-11ee-a0a2-c2d231d02b4e_frame_59.png
            San Servolo pšenično pivo 0,5 l|https://imageproxy.wolt.com/menu/menu-images/shared/4b916b36-d719-11ee-b2ce-e6753cfa82cd_frame_66.png
            San Servolo Gold 0,75 l|https://imageproxy.wolt.com/menu/menu-images/shared/7c74b550-d719-11ee-8d40-1e90adf8221e_frame_65.png
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
               
        
        //this.processProducts(productsImport);        
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
        
    //Temporary function, samo za import slika
    public List<PGSProduct> processProducts(String input) 
    {
        List<PGSProduct> products = new ArrayList<>();
        Scanner scanner = new Scanner(input);

        int i = 0;
        int ok = 0;
        int notFound = 0;
        int multi = 0;
        int corrected = 0;
        Set<String> notFoundSet = new HashSet<>();
        
        while (scanner.hasNextLine()) 
        {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            System.out.println("----------------------------------------------------------------");

            // Koristimo "\\|" jer je | specijalan karakter u regexu
            String[] delovi = line.split("\\|");
            String ime = delovi[0];
            String link = delovi[1];            

            // Rezultat
            System.out.println("Proizvod: " + delovi[0]); // Proizvod
            System.out.println("Looking for product by name");                    
            
            List<PGSProduct> ls = this.productService.findProductsEntites(ime,null,ime);
            
            int found = ls.size();
            
           
            if( found == 1 )
            {   
                logger.info("Found 1");
                ok++;
                PGSProduct p = ls.getFirst();
                p.setImageUrl(link+"?w=1920");
                logger.info("SETTING URL: "+link);
                this.productService.updateProduct(p.getId(), p);
                System.out.println("Product updated");
            }
            else if( found == 0 )
            {
                logger.error("Not found: "+ime);
                notFound++;
                notFoundSet.add(ime);
            }
            else
            {
                multi++;
                logger.warn("found "+found+": "+ime);
                
                Optional<PGSProduct> pronadjeniProizvod = ls.stream()
                .filter(p -> p.getName() != null && p.getName().equals(ime))
                .findFirst();
                
                if (pronadjeniProizvod.isPresent()) 
                {
                    PGSProduct proizvod = pronadjeniProizvod.get();
                    logger.info("Pronađen proizvod sa ID-jem: " + proizvod.getId());
                    proizvod.setImageUrl(link+"?w=1920");
                    logger.info("SETTING URL: "+link);
                    this.productService.updateProduct(proizvod.getId(), proizvod);
                    logger.info("Product updated");
                    corrected++;
                } 
                else 
                {
                    logger.error("Proizvod nije pronađen: "+ime);
                    notFoundSet.add(ime);
                }                
            }
        }
        
        logger.info("OK: "+ok);
        logger.error("NF: "+notFound);
        logger.warn("ML: "+multi);
        logger.info("of which corrected: "+corrected);        
        logger.warn("Pls correct: ");
        logger.warn(notFoundSet.toString());        
        
        scanner.close();
        return products;
    }
}