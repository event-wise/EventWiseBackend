package com.event.eventwiseap.dao;

import com.event.eventwiseap.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogDAO extends JpaRepository<Log, Long> {
}