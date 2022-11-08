package com.event.eventwiseap.service;

import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;

import java.util.List;

public interface GroupService {
    // Create
    Group create(Group group);

    // Read
    Group getById(Long id);
    List<Group> getGroupsByOwner(User user);

    // Update
    Group save(Group group);

    // Delete
    Long delete(Long id);
    Long deleteByOwner(User user);

}
