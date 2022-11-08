package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDAO extends JpaRepository<Event, Long> {
    List<Event> getEventsByAcceptedMembersContaining(User user);


    // @Transactional
    Long removeById(Long id);
}
