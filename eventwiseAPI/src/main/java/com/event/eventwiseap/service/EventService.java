package com.event.eventwiseap.service;

import com.event.eventwiseap.model.Event;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventService {
    // Create and update
    @Transactional
    Event save(Event event);

    // Read
    List<Event> getEventsByUserId(Long userId);
    Event getEventById(Long id);

    // Delete
    Long delete(Long id);
}
