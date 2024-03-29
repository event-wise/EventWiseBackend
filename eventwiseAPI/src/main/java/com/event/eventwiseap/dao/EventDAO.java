package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

public interface EventDAO extends JpaRepository<Event, Long> {
    List<Event> getEventsByAcceptedMembersContaining(User user);

    Event getEventById(Long id);

    Set<Event> getEventsByOrganizerId(Long organizerId);

    List<Event> getEventsByGroupId(Long groupId);
    List<Event> getEventsByOrderByGroupId();

    @Transactional
    Long removeById(Long id);
}
