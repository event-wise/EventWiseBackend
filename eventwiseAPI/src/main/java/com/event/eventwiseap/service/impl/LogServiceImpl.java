package com.event.eventwiseap.service.impl;

import com.event.eventwiseap.dao.LogDAO;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Log;
import com.event.eventwiseap.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogDAO logDAO;

    @Override
    public Log save(Log log) {
        if(Objects.isNull(log)){
            throw new ObjectIsNullException("Log object cannot be null (save)");
        }
        return logDAO.save(log);
    }

    @Override
    public List<Log> getAllByGroupId(Long groupId) {
        if(Objects.isNull(groupId)){
            throw new ObjectIsNullException("Group ID cannot be null (save)");
        }
        return logDAO.getAllByGroupId(groupId);
    }
}
