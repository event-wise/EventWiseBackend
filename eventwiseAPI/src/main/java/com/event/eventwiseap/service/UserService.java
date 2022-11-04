package com.event.eventwiseap.service;

import com.event.eventwiseap.model.User;

import java.util.List;

public interface UserService {
    // Create
    User create(User user);

    // Read
    User getByUsername(String username);
    User getById(Long id);
    List<User> searchByDisplayedName(String displayedName);

    // Update
    User update(User updatedUser);

    // Remove
    Long delete(Long id);
}
