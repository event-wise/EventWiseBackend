package com.event.eventwiseap.service;

import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;

import java.util.List;
import java.util.Set;

public interface GroupService {
    // Create and update
    Group save(Group group);

    // Read
    Group getById(Long id);
    List<Group> getGroupsByOwner(User user);
    Set<Group> getGroupsByMember(User user);

    // Update


    // Delete
    Long delete(Long id);
    Long deleteByOwner(User user);

}
