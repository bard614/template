package com.example.template.template.service;

import com.example.template.template.model.User;
import com.example.template.template.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLogService userLogService;

    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        userLogService.logUserOperation(savedUser, "CREATE_USER");
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            userRepository.deleteById(id);
            userLogService.logUserOperation(user, "DELETE_USER");
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}