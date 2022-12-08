package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogDAO extends JpaRepository<Log, Long> {
    List<Log> getAllByGroupIdOrderByIdDesc(Long id);
}
