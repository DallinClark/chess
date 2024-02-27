package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.GamePlayerData;
import dataAccess.MemoryDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
    }

    @Test
    public void createGame_Success() throws DataAccessException {
        dataAccess.clear();
        UserData test = new UserData("testName","testPassword","testEmail");
        dataAccess.createUser(test);
        int gameId = gameService.createGame("TestGame", dataAccess.createAuth("testName").authToken());
        assertTrue(gameId > 0);
    }

    @Test
    public void createGame_Failure_Unauthorized() {
        String gameName = "Test Game";
        String invalidAuthToken = "invalidToken";
        assertThrows(DataAccessException.class, () -> gameService.createGame(gameName, invalidAuthToken));
    }

    @Test
    public void joinGame_Success() throws DataAccessException {
        dataAccess.clear();
        UserData test = new UserData("testName","testPassword","testEmail");
        dataAccess.createUser(test);
        String authToken = dataAccess.createAuth("testName").authToken();
        int gameId = gameService.createGame("JoinGame", authToken);
        GamePlayerData gamePlayerData = new GamePlayerData("WHITE", gameId);
        assertDoesNotThrow(() -> gameService.joinGame(gamePlayerData, authToken));
    }

    @Test
    public void joinGame_Failure_InvalidGameID() {
        GamePlayerData gamePlayerData = new GamePlayerData("WHITE", -1);
        String authToken = "validToken";
        assertThrows(DataAccessException.class, () -> gameService.joinGame(gamePlayerData, authToken));
    }


    @Test
    public void listGames_Success() throws DataAccessException {
        dataAccess.clear();
        UserData test = new UserData("testName","testPassword","testEmail");
        dataAccess.createUser(test);
        String authToken = dataAccess.createAuth("testName").authToken();
        gameService.createGame("ListGame1", authToken);
        gameService.createGame("ListGame2", authToken);
        GameData[] games = gameService.listGames(authToken);
        assertTrue(games.length == 2);
    }

    @Test
    public void listGames_Failure_Unauthorized() {
        String invalidAuthToken = "invalidToken";
        assertThrows(DataAccessException.class, () -> gameService.listGames(invalidAuthToken));
    }


    @Test
    public void clear_Success() throws DataAccessException {
        dataAccess.clear();
        assertDoesNotThrow(() -> gameService.clear());
    }
}

