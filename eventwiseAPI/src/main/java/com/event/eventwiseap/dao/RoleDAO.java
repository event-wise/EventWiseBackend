package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDAO extends JpaRepository<Role, Long> {
    Role findByName(RoleType name);
}
