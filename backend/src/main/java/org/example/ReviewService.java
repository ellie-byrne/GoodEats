package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Review createReview(Integer userID, Integer restaurantID, String review, Integer rating) {
        int nextId = getNextSequence("reviewId"); // Generate the next ID
        Review newReview = new Review(nextId, userID, restaurantID, new Date(), review, rating, false);
        return reviewRepository.save(newReview);
    }

    private int getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        Counter counter = mongoTemplate.findAndModify(query, update, org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true).upsert(true), Counter.class);
        return counter.getSeq();
    }

    public Review updateReview(Integer id, String reviewText, Integer rating) {
        Review existing = reviewRepository.findById(id).orElse(null);
        if (existing == null) return null;

        if (reviewText != null && !reviewText.trim().isEmpty()) {
            existing.setReview(reviewText);
        }

        if (rating != null) {
            existing.setRating(rating);
        }

        return reviewRepository.save(existing);
    }
}