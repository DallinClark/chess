package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {
    void checkUsername(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    void checkUser(UserData user) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void authorize(String authToken) throws  DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    void joinGame(GamePlayerData game, String authToken) throws  DataAccessException;
    String userFromAuth(String authToken) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void clear() throws DataAccessException;

}
