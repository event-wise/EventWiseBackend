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
    private final UserDAO userDAO;

//    private final GroupService groupService;


//    @Override
//    public List<Event> getEventsByUserId(Long userId) {
//        return null;
//    }
//
//    @Override
//    public Event getEventById(Long id) {
//        if (Objects.isNull(id)) {
//            throw new ObjectIsNullException("Event ID cannot be null");
//        }
//        return eventDAO.getById(id);
//    }
//
//
//    @Override
//    public Long delete(Long id) {
//        if (Objects.isNull(id)) {
//            throw new ObjectIsNullException("Event ID cannot be null");
//        }
//        Event event = eventDAO.getEventById(id);
//        event.removeFromGroup();
//        eventDAO.save(event);
//        groupService.save(event.getGroup());
//        return eventDAO.removeById(id);
//    }


    @Transactional
    @Override
    public Event save(Event event) {
        if(Objects.isNull(event)){
            throw new ObjectIsNullException("Event object cannot be null");
        }

        return eventDAO.save(event);
    }

    @Override
    public Event getEventById(Long id) {
        return null;
    }

    @Override
    public Long delete(Long id) {
        if(Objects.isNull(id)){
            throw new ObjectIsNullException("Event ID cannot be null");
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
    public List<Event> getEventsByUserId(Long userId) {
        return null;
    }
}
