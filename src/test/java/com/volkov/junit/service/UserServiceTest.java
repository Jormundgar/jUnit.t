package com.volkov.junit.service;

import com.volkov.junit.dto.User;
import com.volkov.junit.paramresolver.UserServiceParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("user")
@ExtendWith({
        UserServiceParamResolver.class
})
class UserServiceTest {
    private static final User MIKE = User.of(1, "Mike", "qwerty");
    private static final User LIOR = User.of(2, "Lior", "1234");
    private UserService userService;
    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }
    @BeforeAll
    static void init() {
        System.out.println("Before all ");
    }
    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each " + this);
        this.userService = userService;
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
    @AfterEach
    void deleteDataFromDataBase() {
        System.out.println("After each " + this);
    }
    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all ");
    }
    @Nested
    @DisplayName("Test user login functionality")
    @Tag("login")
    class LoginTest {
        @Test
        void loginSuccessIfUserExists() {
            userService.add(MIKE);
            var maybeUser = userService.login(MIKE.getUsername(), MIKE.getPassword());
            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(MIKE));
        }
        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class,
                                () -> userService.login(null, "Dummy"));
                        assertThat(exception.getMessage()).isEqualTo("Username or Password is null");
                    },
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class,
                                () -> userService.login("Dummy", null));
                        assertThat(exception.getMessage()).isEqualTo("Username or Password is null");
                    }
            );
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
    }
}
