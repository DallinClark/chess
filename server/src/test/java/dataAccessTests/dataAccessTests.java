package dataAccessTests;


import dataAccess.DatabaseManager;
import dataAccess.GamePlayerData;
import dataAccess.SqlDataAccess;
import dataAccess.DataAccessException;
import model.GameData;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class dataAccessTests {

    private SqlDataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new SqlDataAccess(); // Adjust to use a test database
        dataAccess.clear(); // Ensure a clean state before each test
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        dataAccess.clear(); // Clean up after each test
    }

    @Test
    public void sqlDataAccess_Constructor_Pass() {
        // Action & Assert
        assertDoesNotThrow(SqlDataAccess::new);
    }

    @Test
    public void checkUsername_Exists() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        dataAccess.createUser(user);
        assertThrows(DataAccessException.class, () -> dataAccess.checkUsername("username"));
    }

    @Test
    public void checkUsername_NotExists() {
        assertDoesNotThrow(() -> dataAccess.checkUsername("newUsername"));
    }

    @Test
    public void createUser_Success() throws DataAccessException {
        UserData newUser = new UserData("newUser", "password", "newUser@example.com");
        assertDoesNotThrow(() -> dataAccess.createUser(newUser));
        // Further validate by trying to fetch the user or check if count increased
    }

    @Test
    public void createUser_DuplicateUsername() throws DataAccessException {
        UserData user1 = new UserData("username", "password", "email@example.com");
        dataAccess.createUser(user1);
        UserData user2 = new UserData("username", "password2", "email2@example.com");
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(user2));
    }

    @Test
    public void createAuth_ValidUser() throws DataAccessException {
        UserData user = new UserData("userForAuth", "password", "userForAuth@example.com");
        dataAccess.createUser(user);
        assertDoesNotThrow(() -> dataAccess.createAuth("userForAuth"));
        // You may want to verify the token was actually created
    }

    @Test
    public void createAuth_InvalidUser() {
        assertThrows(DataAccessException.class, () -> dataAccess.createAuth("nonExistingUser"));
    }

    @Test
    public void checkUser_ValidCredentials() throws DataAccessException {
        // Setup
        String username = "validUser";
        String password = "securePassword";
        UserData user = new UserData(username, password, "validUser@example.com");
        dataAccess.createUser(user);

        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.checkUser(new UserData(username, password, "")));
    }

    @Test
    public void checkUser_InvalidCredentials() throws DataAccessException {
        // Setup
        String username = "invalidUser";
        String password = "wrongPassword";
        UserData user = new UserData("someUser", "somePassword", "someUser@example.com");
        dataAccess.createUser(user);

        // Action & Assert
        assertThrows(DataAccessException.class, () -> dataAccess.checkUser(new UserData(username, password, "")));
    }

    @Test
    public void deleteAuth_ValidToken() throws DataAccessException {
        // Setup
        String username = "userForDeletion";
        UserData user = new UserData(username, "password", "user@example.com");
        dataAccess.createUser(user);
        AuthData authData = dataAccess.createAuth(username);

        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.deleteAuth(authData.authToken()));
    }

    @Test
    public void deleteAuth_InvalidToken() {
        // Action & Assert
        assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth("nonExistingToken"));
    }

    @Test
    public void authorize_ValidToken() throws DataAccessException {
        // Setup
        String username = "authUser";
        UserData user = new UserData(username, "password", "authUser@example.com");
        dataAccess.createUser(user);
        AuthData authData = dataAccess.createAuth(username);

        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.authorize(authData.authToken()));
    }

    @Test
    public void authorize_InvalidToken() {
        // Action & Assert
        assertThrows(DataAccessException.class, () -> dataAccess.authorize("invalidAuthToken"));
    }

    @Test
    public void listGames_WithExistingGames() throws DataAccessException {
        // Assume createGame and necessary setup is correctly implemented
        // Setup
        String username = "gameListUser";
        UserData user = new UserData(username, "password", "gameListUser@example.com");
        dataAccess.createUser(user);
        String authToken = dataAccess.createAuth(username).authToken();
        dataAccess.createGame("Chess1");
        dataAccess.createGame("Chess2");

        // Action
        GameData[] games = dataAccess.listGames();

        // Assert
        assertTrue(games.length >= 2);
    }

    @Test
    public void listGames_WithoutExistingGames() throws DataAccessException {

        // Action
        GameData[] games = dataAccess.listGames();

        // Assert
        assertEquals(games.length, 0);
    }

    @Test
    public void createGame_Success() throws DataAccessException {
        // Setup
        String username = "gameCreator";
        UserData user = new UserData(username, "password123", "creator@example.com");
        dataAccess.createUser(user);
        String authToken = dataAccess.createAuth(username).authToken();

        // Action
        int gameId = dataAccess.createGame("New Chess Game");

        // Assert
        assertTrue(gameId > 0);
    }

    @Test
    public void createGame_Fail() throws DataAccessException {

        int gameId = dataAccess.createGame("New Chess Game");

        // Assert
        assertTrue(gameId > 0);
    }

    @Test
    public void joinGame_Success() throws DataAccessException {
        // Setup
        String username = "playerJoining";
        UserData user = new UserData(username, "pass", "join@example.com");
        dataAccess.createUser(user);
        String authToken = dataAccess.createAuth(username).authToken();
        int gameId = dataAccess.createGame("Chess Game");

        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.joinGame(new GamePlayerData("WHITE", gameId), authToken));
    }

    @Test
    public void joinGame_Failure_GameNotFound() {
        // Action & Assert
        assertThrows(DataAccessException.class, () -> dataAccess.joinGame(new GamePlayerData("WHITE",-1), "someUser"));
    }

    @Test
    public void userFromAuth_Success() throws DataAccessException {
        // Setup
        String username = "testUser";
        UserData user = new UserData(username, "password123", "test@example.com");
        dataAccess.createUser(user);
        String authToken = dataAccess.createAuth(username).authToken();

        // Action
        String retrievedUsername = dataAccess.userFromAuth(authToken);

        // Assert
        assertEquals(username, retrievedUsername);
    }

    @Test
    public void userFromAuth_Fail_Unauthorized() throws DataAccessException {
        // Setup
        String authToken = "invalidAuthToken"; // An invalid authToken

        // Action & Assert
        assertThrows(DataAccessException.class, () -> dataAccess.userFromAuth(authToken));
    }

    @Test
    public void clear_Success() throws DataAccessException {
        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.clear());

        // Assert: Check if the database is cleared successfully (Implementation-specific assertion)
        // Implement based on how the database state is verified in your system
    }


    @Test
    public void checkUser_Fail_NullUsername() throws DataAccessException {
        // Setup
        UserData user = new UserData(null, "password123", "test@example.com"); // Null username

        // Action & Assert
        assertThrows(DataAccessException.class, () -> dataAccess.checkUser(user));
    }

    @Test
    public void createGame_Success_EmptyGameName() throws DataAccessException {
        // Setup: Empty game name
        String gameName = "";

        // Action
        int gameId = dataAccess.createGame(gameName);

        // Assert: Check if the game ID is valid (non-negative integer indicates success)
        assertTrue(gameId >= 0);
    }

    @Test
    public void checkUsername_Pass_UniqueUsername() throws DataAccessException {
        // Setup: Unique username
        String username = "uniqueUsername";

        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.checkUsername(username));
    }

    @Test
    public void checkUsername_Pass_AlphanumericUsername() throws DataAccessException {
        // Setup: Alphanumeric username
        String username = "alpha123";

        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.checkUsername(username));
    }

    @Test
    public void checkUsername_Pass_SpecialCharactersUsername() throws DataAccessException {
        // Setup: Username with special characters
        String username = "user@example.com";

        // Action & Assert
        assertDoesNotThrow(() -> dataAccess.checkUsername(username));
    }






















}

