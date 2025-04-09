package org.example.Services;

import org.example.Models.Counter;
import org.example.Models.Review;
import org.example.Repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public abstract class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Review createReview(Integer userID, Integer restaurantID, String review, Integer rating) {
        int nextId = getNextSequence("reviewId");

        // Create a new Review object using setter methods instead of constructor
        Review newReview = new Review();
        newReview.setId(nextId);
        newReview.setUserID(userID);
        newReview.setRestaurantID(restaurantID);
        newReview.setDate(new Date());
        newReview.setReview(review);
        newReview.setRating(rating);
        newReview.setFavourite(false);

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

    public abstract Review createReview(int userID, int restaurantID, String review, int rating);

    public abstract Review updateReview(int id, String review, Integer rating);
}