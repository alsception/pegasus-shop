package org.alsception.pegasus.features.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.alsception.pegasus.features.order.PGSOrder;
import org.alsception.pegasus.features.products.PGSProduct;
import lombok.Data;

@Data
public class OrderItemDTO {

    private Long id;
    private Long productId;
    private double quantity;
    private BigDecimal price;

    //Entities for now, should be DTO later

    private PGSOrder order;
    private PGSProduct product;
    
    private LocalDateTime created;
    private LocalDateTime modified;
}
