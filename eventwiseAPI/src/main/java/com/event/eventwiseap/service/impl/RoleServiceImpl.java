package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.RoleDAO;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleDAO roleDAO;

    @Override
    public Role findByName(RoleType name){
        if(Objects.isNull(name)){
            throw new ObjectIsNullException("Cannot search a role with null name");
        }
        return roleDAO.findByName(name);
    }
}
