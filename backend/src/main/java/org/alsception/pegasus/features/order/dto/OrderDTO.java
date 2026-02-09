package org.alsception.pegasus.features.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.alsception.pegasus.features.users.PGSUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Lombook for getters and setters
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private Long userId;
    private PGSUser user;
    private Long tableId;
    private String code;
    private String stol;
    private String name;
    private String address;
    private String paymentMethod;
    private String currency;
    private String status;
    private String comment;
    private BigDecimal price;
    private List<OrderItemDTO> items;
    private LocalDateTime created;
    private LocalDateTime modified;
    private LocalDateTime uPripremiAt;
    private LocalDateTime spremnoAt;
}
