package com.volkov.junit.dao;

import lombok.SneakyThrows;

import java.sql.DriverManager;

public class UserDAO {

    @SneakyThrows
    public boolean delete(Integer userId) {
        try (var connection =
                     DriverManager.getConnection("url", "username", "password")) {
            return true;
        }
    }
}
