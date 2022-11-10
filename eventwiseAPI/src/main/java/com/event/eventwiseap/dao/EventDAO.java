package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface EventDAO extends JpaRepository<Event, Long> {
    List<Event> getEventsByAcceptedMembersContaining(User user);

    Event getEventById(Long id);

     @Transactional
    Long removeById(Long id);
}
