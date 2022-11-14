package com.event.eventwiseap.service;

import com.event.eventwiseap.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    // Create
    User create(User user);

    // Read
    User getByUsername(String username);
    User getById(Long id);
    List<User> searchByDisplayedName(String displayedName);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Update
    @Transactional
    User update(User updatedUser);

    // Remove
    @Transactional
    Long delete(Long id);
}
