package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.EventDAO;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDAO eventDAO;

    @Override
    public Event create(Event event) {
        if (Objects.isNull(event)) {
            throw new ObjectIsNullException("At the creation of an event, object cannot be null");
        }
        return eventDAO.save(event);
    }

    @Override
    public List<Event> getEventsByUserId(Long userId) {
        return null;
    }

    @Override
    public Event getEventById(Long id) {
        return null;
    }

    @Override
    public Event update(Event updatedEvent) {
        if (Objects.isNull(updatedEvent)) {
            throw new ObjectIsNullException("At the creation of an event, object cannot be null");
        }
        return eventDAO.save(updatedEvent);
    }

    @Override
    public Long delete(Long id) {
        return null;
    }
}
