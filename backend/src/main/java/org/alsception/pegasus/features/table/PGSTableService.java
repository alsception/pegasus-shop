package org.alsception.pegasus.features.table;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.alsception.pegasus.features.order.OrderRepository;
import org.alsception.pegasus.features.order.PGSOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PGSTableService 
{
    private int MAX_TABLES_LIMIT = 100;
    
    private final PGSTableRepository tableRepository;
    private final OrderRepository orderRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(PGSTableService.class);

    public PGSTableService(PGSTableRepository tableRepository,OrderRepository orderRepository) 
    {        
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
    }

    public List<PGSTable> getAllTables() {
        return tableRepository.findAll();
    }
    
    public List<PGSTable> getAllTablesWithOccupation() 
    {
        List<PGSTable> tables = tableRepository.findAll();
        List<PGSOrder> orders = orderRepository.findAllWithItems();
    
        // 1. Izvuci sve unikatne brojeve stolova koji su zauzeti u jedan Set (munjevito brzo za pretragu)
        Set<String> occupiedTableNumbers = orderRepository.findAllWithItems().stream()
            .map(PGSOrder::getStol)
            .collect(Collectors.toSet());

        // 2. Samo označi stolove
        tables.forEach(table -> table.setOccupied(occupiedTableNumbers.contains(table.getNumber())));
    
        return tables;
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