package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.repository.WishLikeRepository;
import com.event.scope.eventScope.repository.WishRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishService {
    private final WishRepository wishRepository;
    private final WishLikeRepository wishLikeRepository;

    public WishService(WishRepository wishRepository, WishLikeRepository wishLikeRepository) {
        this.wishRepository = wishRepository;
        this.wishLikeRepository = wishLikeRepository;
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


    public List<Wish> findAllFiltered(List<Long> tagIds, LocalDate createdDate, User filterUser) {

        boolean hasTags = tagIds != null && !tagIds.isEmpty();
        boolean hasDate = createdDate != null;
        boolean hasUser = filterUser != null;

        List<Wish> result;

        if (hasUser) {
            if (!hasTags && !hasDate) {
                result = wishRepository.findAllByUser(filterUser);
            } else if (hasTags && !hasDate) {
                result = wishRepository.findDistinctByUserAndTags_IdIn(filterUser, tagIds);
            } else {
                LocalDateTime start = createdDate.atStartOfDay();
                LocalDateTime end = createdDate.atTime(LocalTime.MAX);
                if (!hasTags) {
                    result = wishRepository.findAllByUserAndCreatedAtBetween(filterUser, start, end);
                } else {
                    result = wishRepository.findDistinctByUserAndTags_IdInAndCreatedAtBetween(filterUser, tagIds, start, end);
                }
            }
        } else {
            if (!hasTags && !hasDate) {
                result = wishRepository.findAll();
            } else if (hasTags && !hasDate) {
                result = wishRepository.findDistinctByTags_IdIn(tagIds);
            } else {
                LocalDateTime start = createdDate.atStartOfDay();
                LocalDateTime end = createdDate.atTime(LocalTime.MAX);
                if (!hasTags) {
                    result = wishRepository.findAllByCreatedAtBetween(start, end);
                } else {
                    result = wishRepository.findDistinctByTags_IdInAndCreatedAtBetween(tagIds, start, end);
                }
            }
        }

        return sortByLikesAndDate(result);
    }

    private List<Wish> sortByLikesAndDate(List<Wish> wishes) {
        return wishes.stream()
                .sorted(Comparator
                        .<Wish, Long>comparing(
                                w -> wishLikeRepository.countByWish(w),
                                Comparator.reverseOrder())
                        .thenComparing(Wish::getCreatedAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
