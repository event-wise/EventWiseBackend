package com.event.eventwiseap.service;

import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface EventService {
    // Create and update
    @Transactional
    Event save(Event event);

    // Read
    Set<Event> getEventsByUser(User user);
    Set<Event> getEventsByOrganizerId(Long organizerId);
    Set<Event> getEventsByGroupId(Long groupId);
    Event getEventById(Long id);


    // Delete
    Long delete(Long id);
}
