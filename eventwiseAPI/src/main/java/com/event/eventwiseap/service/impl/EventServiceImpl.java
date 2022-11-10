package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.EventDAO;
import com.event.eventwiseap.exception.GeneralException;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.service.EventService;
import com.event.eventwiseap.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDAO eventDAO;

    private final GroupService groupService;
    @Override
    public Event save(Event event) {
        if (Objects.isNull(event)) {
            throw new ObjectIsNullException("At the creation of an event, object cannot be null");
        }
        event = eventDAO.save(event);
        groupService.save(event.getGroup());
        return event;
    }

    @Override
    public List<Event> getEventsByUserId(Long userId) {
        return null;
    }

    @Override
    public Event getEventById(Long id) {
        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("Event ID cannot be null");
        }
        return eventDAO.getById(id);
    }


    @Override
    public Long delete(Long id) {
        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("Event ID cannot be null");
        }
        Event event = eventDAO.getEventById(id);
        event.removeFromGroup();
        eventDAO.save(event);
        groupService.save(event.getGroup());
        return eventDAO.removeById(id);
    }
}
