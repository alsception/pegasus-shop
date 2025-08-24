package org.alsception.pegasus.features.products;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "pgs_product_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PGSProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL identity column: autoassign by postgres db
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    // Self-referencing relationship: Many categories can have one parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    @JsonIgnore
    private PGSProductCategory parent;

    // One parent can have many children
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore//We dont need it now for products, but will need it later...
    private Set<PGSProductCategory> children;
}
