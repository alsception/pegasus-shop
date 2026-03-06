package org.alsception.pegasus.features.table;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        //TODO: ovde trebaju orders, ali samo koji nisu vec placeni.
        List<PGSOrder> orders = orderRepository.findAllInActiveSessionWithItems();
        
        logger.trace("Found orders: "+orders.size());
        
        orders.forEach(o -> logger.trace(o.toString()));
    
        // 1. Izvuci sve unikatne brojeve stolova koji su zauzeti u jedan Set (munjevito brzo za pretragu)
        Set<String> occupiedTableNumbers = orders.stream()
            .map(PGSOrder::getStol)
            .collect(Collectors.toSet());

        // 2. Samo označi stolove
        tables.forEach( table -> 
            {
                table.setOccupied(occupiedTableNumbers.contains(table.getNumber()));
            } 
        );
    
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
        int errors = 0;
        try
        {
            logger.info("Creating sample tables");

            List<PGSTable> tables = new ArrayList<>();

            // 001 do 040
            for (int i = 1; i <= 40; i++) {
                String id = String.format("%03d", i);
                tables.add(new PGSTable(id, 0, "", "", ""));
            }

            // 100 do 140
            for (int i = 100; i <= 140; i++) {
                tables.add(new PGSTable(String.valueOf(i), 0, "", "", ""));
            }

            // 900 do 920
            for (int i = 900; i <= 920; i++) {
                tables.add(new PGSTable(String.valueOf(i), 0, "", "", ""));
            }
            
            tableRepository.saveAll(tables);

            logger.info("Tables created: "+tables.size());
        }
        catch(Exception e)
        {
            logger.error("Error generating table");
            logger.error(e.getMessage());
            errors++;
        }
    }
}       