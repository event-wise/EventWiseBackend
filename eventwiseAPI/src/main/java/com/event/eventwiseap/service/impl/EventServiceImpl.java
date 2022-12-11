package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.EventDAO;
import com.event.eventwiseap.dao.UserDAO;
import com.event.eventwiseap.exception.GeneralException;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.service.EventService;
import com.event.eventwiseap.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDAO eventDAO;

    @Transactional
    @Override
    public Event save(Event event) {
        if(Objects.isNull(event)){
            throw new ObjectIsNullException("Event object cannot be null (save)");
        }
        return eventDAO.save(event);
    }

    @Override
    public Event getEventById(Long id) {
        if(Objects.isNull(id)){
            throw new ObjectIsNullException("Event ID cannot be null (event details)");
        }
        return eventDAO.getEventById(id);
    }

    @Override
    public Long delete(Long id) {
        if(Objects.isNull(id)){
            throw new ObjectIsNullException("Event ID cannot be null (delete)");
        }
        Event event = eventDAO.getEventById(id);
        Set<User> users = event.getAcceptedMembers();
        for(User user: users)
            user.rejectEvent(event);
        event.setAcceptedMembers(new HashSet<>());
        eventDAO.save(event);
        return eventDAO.removeById(id);
    }

    @Override
    public Set<Event> getEventsByOrganizerId(Long organizerId) {
        if(Objects.isNull(organizerId)){
            throw new ObjectIsNullException("Organizer ID cannot be null (events by organizer)");
        }

        return eventDAO.getEventsByOrganizerId(organizerId);
    }

    @Override
    public List<Event> getEventsByGroupId(Long groupId) {
        if(Objects.isNull(groupId)){
            throw new ObjectIsNullException("Group ID cannot be null (events by group)");
        }

        return eventDAO.getEventsByGroupId(groupId);
    }

    @Override
    public List<Event> getEventsByUser(User user) {
        if(Objects.isNull(user)){
            throw new ObjectIsNullException("User cannot be null (events by user)");
        }
        return eventDAO.getEventsByAcceptedMembersContaining(user);
    }

    @Override
    public List<Event> getAllEvents(){
        return eventDAO.getEventsByOrderByGroupId();
    }

}
