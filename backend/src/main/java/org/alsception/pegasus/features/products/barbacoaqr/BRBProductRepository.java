package org.alsception.pegasus.features.products.barbacoaqr;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BRBProductRepository extends JpaRepository<BRBProduct, Long> {
    
        List<BRBProduct> findByTitleContainingIgnoreCase(String title);

}