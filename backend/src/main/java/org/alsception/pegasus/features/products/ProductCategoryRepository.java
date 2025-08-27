package org.alsception.pegasus.features.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<PGSProductCategory, Long> {

    List<PGSProductCategory> findByParentIsNull();
    
}