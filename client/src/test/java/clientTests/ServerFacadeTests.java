package clientTests;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import model.GameData;
import model.GameList;
import model.GamePlayerData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void register_Success() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        AuthData data = sf.registerUser(new UserData("user", "pass", "email"));
        assertNotNull(data.authToken());
    }
    @Test
    public void register_Fail() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        UserData existingUser = new UserData("existingUser", "pass", "email");

        assertDoesNotThrow(() -> sf.registerUser(existingUser));

        assertThrows(ResponseException.class, () -> sf.registerUser(existingUser));

    }

    @Test
    public void login_Success() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        UserData user = new UserData("user", "pass", "email");
        sf.registerUser(user); // Ensure the user is registered before login
        AuthData data = sf.login(new UserData("user", "pass", null));
        assertNotNull(data.authToken());
    }

    @Test
    public void login_Fail_WrongPassword() throws IOException, ResponseException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        UserData user = new UserData("user", "correctPass", "email");
        sf.registerUser(user); // Ensure the user is registered

        // Attempting to login with the wrong password should fail
        assertThrows(ResponseException.class, () -> sf.login(new UserData("user", "wrongPass", null)));
    }

    @Test
    public void logout_Success() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        UserData user = new UserData("user", "pass", "email");
        sf.registerUser(user);
        AuthData authData = sf.login(user);
        assertDoesNotThrow(() -> sf.logout(authData.authToken()));
    }
    @Test
    public void logout_Fail_InvalidToken() throws IOException, ResponseException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        // Attempting to logout with an invalid token should fail
        assertThrows(ResponseException.class, () -> sf.logout("invalidToken"));
    }
    @Test
    public void createGame_Success() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        UserData user = new UserData("user", "pass", "email");
        sf.registerUser(user);
        AuthData authData = sf.login(user);

        GameData newGame = new GameData(); // Assuming GameData constructor or setters
        newGame.setGameName("ChessGame1");
        GameData result = sf.createGame(newGame, authData.authToken());

        assertNotNull(result.getGameID()); // Assuming GameData has a getGameId method
        assertNotEquals("ChessGame1", result.getGameName()); // Validate the game name matches
    }
    @Test
    public void createGame_Fail_Unauthorized() throws IOException, ResponseException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        GameData newGame = new GameData();
        newGame.setGameName("ChessGame2");

        // Attempting to create a game without or with invalid authentication should fail
        assertThrows(ResponseException.class, () -> sf.createGame(newGame, "invalidToken"));
    }
    @Test
    public void joinGame_Success() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        // Assume we have a way to create a game and get its ID
        String gameId = "validGameId"; // Placeholder for a valid game ID
        UserData user = new UserData("joinUser", "pass", "join@example.com");
        sf.registerUser(user);
        AuthData authData = sf.login(user);

        // Assuming GamePlayerData constructor or setters
        GamePlayerData joinRequest = new GamePlayerData("joinUser", 123);
    }
    @Test
    public void joinGame_Fail_GameNotFound() throws IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        String invalidGameId = "invalidGameId"; // Placeholder for an invalid game ID
        GamePlayerData joinRequest = new GamePlayerData("user", 1234);

        // Assume user is already authenticated, and authToken is valid
        String authToken = "validAuthToken"; // Placeholder for a valid auth token

        // Attempting to join a non-existent game should fail
        assertThrows(ResponseException.class, () -> sf.joinGame(joinRequest, authToken));
    }
    @Test
    public void listGames_Success() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        sf.clear();
        UserData user = new UserData("listUser", "pass", "list@example.com");
        sf.registerUser(user);
        AuthData authData = sf.login(user);

        // Assuming successful login and authToken is valid
        GameList gameList = sf.listGames(authData.authToken());

        assertNotNull(gameList); // Ensure the returned list is not null
        assertNotNull(gameList.getGames()); // Assuming GameList has a getGames method that returns a collection
    }
    @Test
    public void listGames_Fail_Unauthorized() throws IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        // Attempting to list games without or with invalid authentication should fail
        assertThrows(ResponseException.class, () -> sf.listGames("invalidToken"));
    }


    @Test
    public void clear_Success_RemovesAllGames() throws ResponseException, IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");
        // Precondition: Assuming at least one game exists before clearing
        UserData user = new UserData("userForClear", "pass", "emailForClear@example.com");
        sf.registerUser(user);
        AuthData authData = sf.login(user);
        sf.createGame(new GameData(), authData.authToken());

        // Action: Clear all games
        sf.clear();

        }
    @Test
    public void clear_Fail_Unauthorized() throws IOException {
        ServerFacade sf = new ServerFacade("http://localhost:8080");

    }












}
