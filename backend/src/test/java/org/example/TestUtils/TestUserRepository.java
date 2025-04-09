// src/test/java/org/example/TestUtils/TestUserRepository.java
package org.example.TestUtils;

import org.example.Models.User;
import org.example.Repositories.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class TestUserRepository implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public <S extends User> S save(S entity) {
        users.put(entity.getId(), entity);
        usersByUsername.put(entity.getUsername(), entity);
        usersByEmail.put(entity.getEmail(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsById(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public <S extends User> S insert(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> insert(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public List<User> findAllById(Iterable<Integer> ids) {
        List<User> result = new ArrayList<>();
        ids.forEach(id -> {
            if (users.containsKey(id)) {
                result.add(users.get(id));
            }
        });
        return result;
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public void deleteById(Integer id) {
        User user = users.get(id);
        if (user != null) {
            usersByUsername.remove(user.getUsername());
            usersByEmail.remove(user.getEmail());
            users.remove(id);
        }
    }

    @Override
    public void delete(User entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        users.clear();
        usersByUsername.clear();
        usersByEmail.clear();
    }

    // Remaining methods from CrudRepository that we don't need for testing
    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented for test");
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Not implemented for test");
    }
}