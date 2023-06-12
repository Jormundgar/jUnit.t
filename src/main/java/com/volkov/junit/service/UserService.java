package com.volkov.junit.service;

import com.volkov.junit.dao.UserDAO;
import com.volkov.junit.dto.User;

import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class UserService {

    private final List<User> users = new ArrayList<>();
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean delete(Integer userId) {
        return userDAO.delete(userId);
    }

    public List<User> getAll() {
        return users;
    }
    public void add(User ... users) {
        this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {
        if(username == null || password == null) {
            throw new IllegalArgumentException("Username or Password is null");
        }
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
                .collect(toMap(User::getId, identity()));
    }
}
