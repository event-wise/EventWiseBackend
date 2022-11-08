package com.event.eventwiseap.service;

import com.event.eventwiseap.model.Event;

import java.util.List;

public interface EventService {
    // Create
    Event create(Event event);

    // Read
    List<Event> getEventsByUserId(Long userId);
    Event getEventById(Long id);

    // Update
    Event update(Event updatedEvent);

    // Delete
    Long delete(Long id);
}
