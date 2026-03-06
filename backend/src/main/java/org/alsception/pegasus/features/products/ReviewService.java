package org.alsception.pegasus.features.products;

import java.util.List;
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