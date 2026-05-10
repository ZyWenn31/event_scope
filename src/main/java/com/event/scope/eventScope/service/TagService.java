package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.Tag;
import com.event.scope.eventScope.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag findById(Long id) {
        return tagRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag with id " + id + "not found"));
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    public void delete(Long id) {
        tagRepository.deleteById(id);
    }

    public Tag update(Tag tag, Long id) {
        Tag existing = tagRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag with id " + id + "not found"));

        existing.setName(tag.getName());
        existing.setEvents(tag.getEvents());
        existing.setWishes(tag.getWishes());

        return tagRepository.save(existing);

    }

    public Set<Tag> findAllByIds(List<Long> ids) {

        return new HashSet<>(
                tagRepository.findAllById(ids)
        );
    }
}
