package org.alsception.pegasus.features.products;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_reviews")
public class PGSReview 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
           
    private String reviewer;
    
    @Column(columnDefinition = "TEXT")
    private String text;
    
    @Column(nullable = false)
    private int rating; // 1-5
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private PGSProduct product;
}
