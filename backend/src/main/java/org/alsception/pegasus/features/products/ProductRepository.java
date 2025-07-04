package org.alsception.pegasus.features.products;

import java.util.List;
import org.alsception.pegasus.features.products.PGSProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<PGSProduct, Long> 
{
    public List<PGSProduct> findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(String code, String name);       
    
    @Query(nativeQuery = true, value = """
        SELECT
            PRODUCT_ID,
            NAME,
            ROUND(AVG(RATING), 1) AS AVERAGE_RATING                         
        FROM
            PGS_REVIEWS 
        LEFT JOIN
            PGS_PRODUCTS 
                ON PGS_REVIEWS.PRODUCT_ID = PGS_PRODUCTS.ID 
        GROUP BY
            PRODUCT_ID 
        ORDER BY
            AVERAGE_RATING DESC 
        LIMIT
            3;""")
    public List<Object[]> getPopularProducts();
}