//package org.alsception.pegasus.features.order;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;
//import jakarta.persistence.Table;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.alsception.pegasus.features.users.PGSUser;
//import org.alsception.pegasus.features.table.PGSTable;
//
//@Data //Lombook for getters and setters
//@NoArgsConstructor
//public class __PGSOrderDTO
//{
//    @Id
//    //@GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    //@JsonBackReference
//    private PGSUser user;
//    
//    private String code;
//    private String email;
//    private String name;
//    private String address;
//    private String paymentMethod;
//    private String currency;
//    private String status;
//    private String comment;
//    
//    private BigDecimal price; 
//    
//    private List<PGSOrderItemDTO> items = new ArrayList<>();
//    
//    private PGSTable table;
//
//    private LocalDateTime created;
//    
//    private LocalDateTime modified;  
//    
//} 

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
}
