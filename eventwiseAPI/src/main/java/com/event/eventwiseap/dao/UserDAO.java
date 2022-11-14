package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface UserDAO extends JpaRepository<User, Long> {
    User getUserById(Long id);
    User getUserByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    List<User> getUsersByGroupsContains(Group group);

    @Transactional
    Long removeUserById(Long id);

    List<User> findUserByDisplayedNameContaining(String displayedName);
}
