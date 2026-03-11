package org.alsception.pegasus.features.statistics;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/total-orders")
    public List<Map<String, Object>> getTotalOrders() 
    {
        return statisticsService.getTotalOrders();
    }

    @GetMapping("/total-amount")
    public List<Map<String, Object>> getTotalAmount() 
    {
        return statisticsService.getTotalAmount();
    }

    @GetMapping("/total-times")
    public List<Map<String, Object>> getTotalTimes() 
    {
        return statisticsService.getTimes();
    }
}