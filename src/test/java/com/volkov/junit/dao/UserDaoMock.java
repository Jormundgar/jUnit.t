package com.volkov.junit.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoMock extends UserDAO {

    private Map<Integer, Boolean> answers = new HashMap<>();

    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId, false);
    }
}
