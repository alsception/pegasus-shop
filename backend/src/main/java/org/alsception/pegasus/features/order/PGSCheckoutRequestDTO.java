package org.alsception.pegasus.features.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_checkout_request")
public class PGSCheckoutRequestDTO
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
     
    private String email;
    private String name;
    private String address;
    private String paymentMethod;
    private String comment;
}