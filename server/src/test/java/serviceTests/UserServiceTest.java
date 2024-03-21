package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.SqlDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new SqlDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    public void registerUser_Success() throws DataAccessException {
        dataAccess.clear();
        UserData newUser = new UserData("testUser", "testPass","testEmail");
        AuthData authData = userService.registerUser(newUser);
        assertNotNull(authData);
        dataAccess.authorize(authData.authToken());
    }

    @Test
    public void registerUser_Failure_AlreadyExists() {
        UserData newUser = new UserData("testUser", "testPass","testEmail");
        assertThrows(DataAccessException.class, () -> {
            userService.registerUser(newUser);
            userService.registerUser(newUser); // Attempt to register again
        });
    }

    @Test
    public void loginUser_Success() throws DataAccessException {
        dataAccess.clear();
        UserData newUser = new UserData("testLogin", "testPass","testEmail");
        userService.registerUser(newUser);
        AuthData authData = userService.login(newUser);
        assertNotNull(authData);
    }

    @Test
    public void loginUser_Failure_WrongCredentials() {
        UserData newUser = new UserData("testLoginFail", "testPass","testEmail");
        UserData wrongUser = new UserData("testLoginFail", "wrongPass","wrongEmail");
        assertThrows(DataAccessException.class, () -> {
            userService.registerUser(newUser);
            userService.login(wrongUser);
        });
    }

    @Test
    public void logout_Success() throws DataAccessException {
        dataAccess.clear();
        UserData newUser = new UserData("testLogout", "testPass","testEmail");
        AuthData authData = userService.registerUser(newUser);
        assertDoesNotThrow(() -> userService.logout(authData.authToken()));
    }
    @Test
    public void logout_Failure_InvalidToken() {
        String invalidToken = "invalidToken";
        assertThrows(DataAccessException.class, () -> userService.logout(invalidToken));
    }
}
