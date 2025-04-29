package org.example.Services;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.example.Factories.ReviewFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

@Service
public class ReviewService {

    public final ReviewRepository reviewRepository;
    public final MongoTemplate mongoTemplate;

    public ReviewService(ReviewRepository reviewRepository, MongoTemplate mongoTemplate) {
        this.reviewRepository = reviewRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Review createReview(Integer userID, Integer restaurantID, String review, Integer rating) {
        int nextId = getNextSequence("reviewId"); // Generate the next ID
        Review newReview = ReviewFactory.create(nextId, userID, restaurantID, review, rating, false);
        return reviewRepository.save(newReview);
    }

    int getNextSequence(String seqName) {
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