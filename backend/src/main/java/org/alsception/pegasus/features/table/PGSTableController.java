package org.alsception.pegasus.features.table;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class PGSTableController 
{
    private final PGSTableService tableService;

    public PGSTableController(PGSTableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<PGSTable> getAllTables() {
        return tableService.getAllTables();
    }

    @PostMapping
    public PGSTable createTable(@RequestBody PGSTable table) {
        return tableService.saveTable(table);
    }

    @DeleteMapping("/{id}")
    public void deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
    }
}