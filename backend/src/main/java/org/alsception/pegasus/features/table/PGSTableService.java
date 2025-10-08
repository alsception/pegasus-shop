package org.alsception.pegasus.features.table;

import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PGSTableService 
{
    private int MAX_TABLES_LIMIT = 100;
    
    private final PGSTableRepository tableRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(PGSTableService.class);

    public PGSTableService(PGSTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<PGSTable> getAllTables() {
        return tableRepository.findAll();
    }

    public PGSTable saveTable(PGSTable table) {
        return tableRepository.save(table);
    }

    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }
    
    public void generateSampleTables() 
    {
        if (tableRepository.count() == 0) 
        { 
            int errors = 0;
            {
                try{
                    logger.info("Creating sample tables");
                    PGSTable table1 = new PGSTable("100", 6, "", "levo", "terasa");
                    PGSTable table2 = new PGSTable("101", 5, "", "desno", "terasa");
                    PGSTable table3 = new PGSTable("120", 6, "", "levo", "terasa");
                    PGSTable table4 = new PGSTable("129", 6, "", "desno", "terasa");
                    PGSTable table5 = new PGSTable("001", 12, "", "desno", "unutra");
                    
                    tableRepository.save(table1);
                    tableRepository.save(table2);
                    tableRepository.save(table3);
                    tableRepository.save(table4);
                    tableRepository.save(table5);                    
                }
                catch(Exception e)
                {
                    logger.error("Error generating table");
                    logger.error(e.getMessage());
                    errors++;
                }
            }
            
            logger.info("✅ Sample tables initialized: ");
            logger.info("Errors: "+errors);
        }
        else
        {
            logger.warn("Sample tables not created. Repository already contains some tables or illegal count requested.");
        }
    }
}