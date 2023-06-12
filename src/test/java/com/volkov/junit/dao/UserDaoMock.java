package com.volkov.junit.dao;

public class UserDaoMock extends UserDAO {
    @Override
    public boolean delete(Integer userId) {
        return false;
    }
}
