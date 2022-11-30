package com.event.eventwiseap.service;


import com.event.eventwiseap.model.Log;

import java.util.List;

public interface LogService {
    // Create and update
    Log save(Log log);

    // Read
    List<Log> getAllByGroupId(Long groupId);
}
