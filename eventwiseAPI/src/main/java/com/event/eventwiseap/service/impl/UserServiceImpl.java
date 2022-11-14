package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.EventDAO;
import com.event.eventwiseap.dao.UserDAO;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.service.EventService;
import com.event.eventwiseap.service.GroupService;
import com.event.eventwiseap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    private final GroupService groupService;

    private final EventService eventService;

    @Override
    //@Transactional
    public User create(User user){
        if (Objects.isNull(user)) {
            throw new ObjectIsNullException("At the creation of a user, object cannot be null");
        }
        return userDAO.save(user);
    }

    @Override
    public User getByUsername(String username) {
        if (Objects.isNull(username)) {
            throw new ObjectIsNullException("Username cannot be null");
        }
        return userDAO.getUserByUsername(username);
    }

    @Override
    public User getById(Long id) {
        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("ID cannot be null");
        }
        return userDAO.getUserById(id);
    }

    @Override
    public List<User> searchByDisplayedName(String displayedName) {
        if (Objects.isNull(displayedName)) {
            throw new ObjectIsNullException("Displayed name cannot be null");
        }
        return userDAO.findUserByDisplayedNameContaining(displayedName);
    }

    @Override
    public User update(User updatedUser) {
        if (Objects.isNull(updatedUser)) {
            throw new ObjectIsNullException("At the creation of a user, object cannot be null");
        }
        return userDAO.save(updatedUser);
    }

    @Override
    public Long delete(Long id) {
        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("ID cannot be null");
        }
        Set<Event> organized = eventService.getEventsByOrganizerId(id);
        for(Event event:organized)
            eventService.delete(event.getId());


        User user = userDAO.getUserById(id);
        Set<Group> groups = new HashSet<>(user.getGroups());


        for(Group group: groups){
            group.removeMember(user);
            if (group.isEmpty()){
                groupService.delete(group.getId());
                continue;
            }
            if(group.isOwner(user))
                group.assignOwner();
            groupService.save(group);
        }
        return userDAO.removeUserById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (Objects.isNull(email)) {
            throw new ObjectIsNullException("Search param cannot be null");
        }
        return userDAO.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (Objects.isNull(username)) {
            throw new ObjectIsNullException("Search param cannot be null");
        }
        return userDAO.existsByUsername(username);
    }
}
