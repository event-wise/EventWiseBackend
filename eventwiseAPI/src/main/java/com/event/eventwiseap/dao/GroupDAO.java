package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface GroupDAO extends JpaRepository<Group, Long> {
    Group getGroupById(Long id);
    List<Group> getGroupsByGroupMembersContaining(User user);

    List<Group> getAllByOwner(User user);



    Long deleteGroupByOwner(User user);

    List<Group> getGroupByOrderByGroupName();

    @Transactional
    Long removeById(Long id);

    boolean existsByGroupName(String groupName);

    boolean existsById(Long id);

}
