package com.volkov.junit.service;

import com.volkov.junit.dto.User;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private static final User MIKE = User.of(1, "Mike", "qwerty");
    private static final User LIOR = User.of(2, "Lior", "1234");
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
        userService.add(MIKE, LIOR);
        var users = userService.getAll();
        assertThat(users).hasSize(2);
    }
    @Test
    void usersConvertedToMapById() {
        userService.add(MIKE, LIOR);
        var users = userService.getAllConvertedById();
        assertAll(
                () -> assertThat(users).containsKeys(MIKE.getId(), LIOR.getId()),
                () -> assertThat(users).containsValues(MIKE, LIOR)
        );
    }
    @Test
    void loginSuccessIfUserExists() {
        userService.add(MIKE);
        var maybeUser = userService.login(MIKE.getUsername(), MIKE.getPassword());
        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertThat(user).isEqualTo(MIKE));
    }
    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(MIKE);
        var maybeUser = userService.login(MIKE.getUsername(), "Dummy");
        assertTrue(maybeUser.isEmpty());
    }
    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(MIKE);
        var maybeUser = userService.login("Dummy", MIKE.getPassword());
        assertTrue(maybeUser.isEmpty());
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
