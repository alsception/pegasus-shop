package org.alsception.pegasus.features.products;

import java.util.List;
import org.alsception.pegasus.features.products.PGSReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController 
{
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<PGSReview> getReviewsByProductId(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    @PostMapping
    public PGSReview createReview(@RequestBody PGSReview review) {
        return reviewService.saveReview(review);
    }
}
