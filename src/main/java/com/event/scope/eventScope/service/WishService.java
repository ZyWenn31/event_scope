package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.repository.WishRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishService {
    private final WishRepository wishRepository;

    public WishService(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    public Wish findById(Long id) {
        return wishRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Wish with id " + id + " not found"));
    }

    public List<Wish> findAll(){
        return wishRepository.findAll();
    }

    public Wish save(Wish wish) {
        return wishRepository.save(wish);
    }

    public void deleteById(Long id) {
        wishRepository.deleteById(id);
    }

    public Wish update(Wish wish, Long id) {
        Wish existing = wishRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Wish with id " + id + " not found"));

        existing.setCreatedAt(wish.getCreatedAt());
        existing.setDescription(wish.getDescription());
        existing.setTags(wish.getTags());
        existing.setUser(wish.getUser());
        existing.setTitle(wish.getTitle());

        return wishRepository.save(existing);
    }
}
