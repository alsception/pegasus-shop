package org.alsception.pegasus.features.order;

import jakarta.persistence.*;
import lombok.Data;

@Data //Lombook for getters and setters
@Entity
@Table(name = "pgs_addresses")
public class PGSAddress 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String city;
    private String postalCode;
    private String country;
}