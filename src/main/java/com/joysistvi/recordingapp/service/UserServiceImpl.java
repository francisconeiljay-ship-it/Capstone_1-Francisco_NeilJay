package com.joysistvi.recordingapp.service;

import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Collections;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllActiveUsers() {
        return userRepository.findAllActive();
    }

    @Override
    public List<User> getAllArchivedUsers() {
        return userRepository.findAllArchived();
    }

    @Override
    public boolean createUser(String username, String plainPassword, String email) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Validation Error: Username cannot be blank.");
            return false;
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            System.out.println("Validation Error: Password must be at least 6 characters.");
            return false;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("Validation Error: Invalid email format.");
            return false;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        User user = new User(username.trim(), hashedPassword, email.trim(), "USER");
        return userRepository.save(user);
    }

    @Override
    public boolean updateUser(int id, String username, String email) {
        if (id <= 0) {
            System.out.println("Validation Error: User ID must be positive.");
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Validation Error: Username cannot be blank.");
            return false;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("Validation Error: Invalid email format.");
            return false;
        }

        User user = new User(id, username.trim(), null, email.trim(), null, false);
        return userRepository.update(user);
    }

    @Override
    public boolean deleteUser(int id) {
        if (id <= 0) {
            System.out.println("Validation Error: User ID must be positive.");
            return false;
        }
        return userRepository.deleteById(id);
    }

    @Override
    public boolean archiveUser(int id) {
        if (id <= 0) {
            System.out.println("Validation Error: User ID must be positive.");
            return false;
        }
        return userRepository.archiveById(id);
    }

    @Override
    public boolean restoreUser(int id) {
        if (id <= 0) {
            System.out.println("Validation Error: User ID must be positive.");
            return false;
        }
        return userRepository.restoreById(id);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Validation Error: Search keyword cannot be blank.");
            return Collections.emptyList();
        }
        return userRepository.searchByUsername(keyword.trim());
    }

    @Override
    public User loginUser(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            System.out.println("Validation Error: Username and password are required.");
            return null;
        }

        User user = userRepository.findByUsername(username.trim());
        if (user != null && BCrypt.checkpw(plainPassword, user.getPassword())) {
            return user;
        }
        return null;
    }
}
