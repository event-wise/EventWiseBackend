package com.event.eventwiseap.service;

import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;

public interface RoleService {
    Role findByName(RoleType name);
}
