package com.example.template.template.service;

import com.example.template.template.model.User;
import com.example.template.template.model.UserLog;
import com.example.template.template.repository.UserLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserLogService {

    @Autowired
    private UserLogRepository userLogRepository;

    public void logUserOperation(User user, String operationType) {
        UserLog userLog = new UserLog(user, operationType, LocalDateTime.now());
        userLogRepository.save(userLog);
    }

    public List<UserLog> getAllUserLogs() {
        return userLogRepository.findAll();
    }
}