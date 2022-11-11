package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.GroupDAO;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;
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

    @Override
    public Group create(Group group) {
        if (Objects.isNull(group)) {
            throw new ObjectIsNullException("At the creation of a group, object cannot be null");
        }
        return groupDAO.save(group);
    }

    @Override
    public Group getById(Long id) {
        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("User cannot be null");
        }
        return groupDAO.getById(id);
    }

    @Override
    public List<Group> getGroupsByOwner(User user) {
        if (Objects.isNull(user)) {
            throw new ObjectIsNullException("User cannot be null");
        }
        return groupDAO.getAllByOwner(user);
    }

    @Override
    public Group save(Group group) {
        if (Objects.isNull(group)) {
            throw new ObjectIsNullException("At the creation of a group, object cannot be null");
        }
        return groupDAO.save(group);
    }


    @Override
    public Long delete(Long id) {
        // delete all events then delete group

        if (Objects.isNull(id)) {
            throw new ObjectIsNullException("When deleting a group, id cannot be null");
        }
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
            throw new ObjectIsNullException("When deleting a group, user cannot be null");
        }

        return groupDAO.deleteGroupByOwner(user);
    }
}
