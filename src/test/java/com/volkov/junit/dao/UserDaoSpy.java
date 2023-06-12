package com.volkov.junit.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDAO {
    private final UserDAO userDAO;
    private Map<Integer, Boolean> answers = new HashMap<>();
    public UserDaoSpy(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId, userDAO.delete(userId));
    }
}
