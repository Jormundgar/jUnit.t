package com.volkov.junit.service;

import com.volkov.junit.dao.UserDAO;
import com.volkov.junit.dto.User;
import com.volkov.junit.extension.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.RepeatedTest.LONG_DISPLAY_NAME;
import static org.mockito.Mockito.*;

@Tag("user")
@ExtendWith({
        UserServiceParamResolver.class,
        GlobalExtension.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        MockitoExtension.class
//        ThrowableExtension.class
})
class UserServiceTest {
    private static final User MIKE = User.of(1, "Mike", "qwerty");
    private static final User LIOR = User.of(2, "Lior", "1234");
    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;
    @Mock
    private UserDAO userDAO;
    @InjectMocks
    private UserService userService;
    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }
    @BeforeAll
    static void init() {
        System.out.println("Before all ");
    }
    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
//        this.userDAO = Mockito.spy(new UserDAO());
//        this.userService = new UserService(userDAO);
    }
    @Test
    void shouldDeleteExistedUser() {
        userService.add(LIOR);
        doReturn(true).when(userDAO).delete(LIOR.getId());
        var deleteResult = userService.delete(LIOR.getId());
//        var argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(userDAO, times(1)).delete(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(LIOR.getId());
        assertThat(deleteResult).isTrue();
    }
    @Test
    @Disabled("Test this option")
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1 " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), "User list should be empty");
    }
    @Test
    @RepeatedTest(value = 5, name = LONG_DISPLAY_NAME)
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
        @ParameterizedTest(name = "{displayName} {index}")
        @MethodSource("getArgumentsForLoginTest")
        @DisplayName("Login Param Test")
        void loginParameterizingTest(String username, String password, Optional<User> user) {
            userService.add(MIKE, LIOR);
            var maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }
        static Stream<Arguments> getArgumentsForLoginTest() {
            return Stream.of(
                    Arguments.of("Mike", "qwerty", Optional.of(MIKE)),
                    Arguments.of("Lior", "1234", Optional.of(LIOR)),
                    Arguments.of("Mike", "Dummy", Optional.empty()),
                    Arguments.of("Dummy", "1234", Optional.empty())
            );
        }
    }
}
