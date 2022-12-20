package com.event.eventwiseap.service;

import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;

import java.util.List;

public interface GroupService {
    // Create and update
    Group save(Group group);
    void removeMember(Group group, User user);

    // Read
    Group getById(Long id);
    List<Group> getGroupsByOwner(User user);
    List<Group> getGroupsByMember(User user);

    // Update
    List<Group> getAllGroups();

    boolean existsByGroupName(String groupName);
    // Delete
    Long delete(Long id);
    Long deleteByOwner(User user);
    boolean existsByGroupId(Long id);
}
