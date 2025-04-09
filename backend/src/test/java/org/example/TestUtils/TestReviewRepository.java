// src/test/java/org/example/TestUtils/TestReviewRepository.java
package org.example.TestUtils;

import org.example.Models.Review;
import org.example.Repositories.ReviewRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestReviewRepository implements ReviewRepository {
    private final Map<Integer, Review> reviews = new HashMap<>();

    @Override
    public List<Review> findByRestaurantID(int restaurantID) {
        return reviews.values().stream()
                .filter(review -> review.getRestaurantID() == restaurantID)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByRestaurantID(Integer restaurantID) {
        return List.of();
    }

    @Override
    public <S extends Review> S save(S entity) {
        reviews.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Review> findById(Integer id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public boolean existsById(Integer id) {
        return reviews.containsKey(id);
    }

    @Override
    public List<Review> findAll() {
        return new ArrayList<>(reviews.values());
    }

    @Override
    public List<Review> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public <S extends Review> S insert(S entity) {
        return null;
    }

    @Override
    public <S extends Review> List<S> insert(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public List<Review> findAllById(Iterable<Integer> ids) {
        List<Review> result = new ArrayList<>();
        ids.forEach(id -> {
            if (reviews.containsKey(id)) {
                result.add(reviews.get(id));
            }
        });
        return result;
    }

    @Override
    public long count() {
        return reviews.size();
    }

    @Override
    public void deleteById(Integer id) {
        reviews.remove(id);
    }

    @Override
    public void delete(Review entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Review> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        reviews.clear();
    }

    // Remaining methods from CrudRepository that we don't need for testing
    @Override
    public <S extends Review> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends Review> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends Review> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends Review> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends Review> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends Review> long count(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends Review> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends Review, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Not implemented for test");
    }
}