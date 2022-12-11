package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.GroupDAO;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.service.EventService;
import com.event.eventwiseap.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupDAO groupDAO;

    private final EventService eventService;

    @Override
    public void removeMember(Group group, User user) {
        if (Objects.isNull(user)) {
            throw new ObjectIsNullException("User cannot be null (group details)");
        }
        if (Objects.isNull(group)) {
            throw new ObjectIsNullException("Group cannot be null (group details)");
        }
        group.removeMember(user);
        if(group.isEmpty()){
            delete(group.getId());
            return;
        }
        groupDAO.save(group);
    }

    @Override
    public Group getById(Long id) {
        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("Group ID cannot be null (group details)");
        }
        return groupDAO.getById(id);
    }

    @Override
    public List<Group> getGroupsByOwner(User user) {
        if (Objects.isNull(user)) {
            throw new ObjectIsNullException("User cannot be null (group by owner)");
        }
        return groupDAO.getAllByOwner(user);
    }

    @Override
    public Set<Group> getGroupsByMember(User user) {
        if (Objects.isNull(user)) {
            throw new ObjectIsNullException("User cannot be null (group by owner)");
        }
        return groupDAO.getGroupsByGroupMembersContaining(user);
    }

    @Override
    public Group save(Group group) {
        if (Objects.isNull(group)) {
            throw new ObjectIsNullException("Group cannot be null (save)");
        }
        return groupDAO.save(group);
    }


    @Override
    public Long delete(Long id) {

        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("Group ID cannot be null (delete)");
        }
        List<Event> groupEvents = eventService.getEventsByGroupId(id);
        for(Event event: groupEvents)
            eventService.delete(event.getId());

        Group group = groupDAO.getGroupById(id);
        Set<User> users = group.getGroupMembers();
        for (User user:users)
            user.removeGroup(group);
        group.setGroupMembers(new HashSet<>());
        groupDAO.save(group);
       return groupDAO.removeById(id);
    }

    @Override
    public Long deleteByOwner(User user) {
        if (Objects.isNull(user)) {
            throw new ObjectIsNullException("User cannot be null (delete by owner)");
        }

        return groupDAO.deleteGroupByOwner(user);
    }

    @Override
    public List<Group> getAllGroups(){return groupDAO.getGroupByOrderByGroupName();}

    @Override
    public boolean existsByGroupName(String groupName) {
        if (Objects.isNull(groupName)) {
            throw new ObjectIsNullException("Search param cannot be null");
        }
        return groupDAO.existsByGroupName(groupName);
    }

}
