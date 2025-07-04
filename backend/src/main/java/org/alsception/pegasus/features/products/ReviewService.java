package org.alsception.pegasus.features.products;

import java.util.List;
import org.alsception.pegasus.features.products.PGSReview;
import org.alsception.pegasus.features.products.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public List<PGSReview> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
    
    public PGSReview saveReview(PGSReview review) {
        return reviewRepository.save(review);
    }
}