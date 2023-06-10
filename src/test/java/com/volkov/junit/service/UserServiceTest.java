package com.volkov.junit.service;

import com.volkov.junit.dto.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    @BeforeAll
    static void init() {
        System.out.println("Before all ");
    }
    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
        userService = new UserService();
    }
    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1 " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), "User list should be empty");
    }
    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2 " + this);
        userService.add(new User());
        userService.add(new User());
        var users = userService.getAll();
        assertEquals(2, users.size());
    }
    @AfterEach
    void deleteDataFromDataBase() {
        System.out.println("After each " + this);
    }
    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all ");
    }

}
