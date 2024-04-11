package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.GamePlayerData;
import model.UserData;

public interface DataAccess {
    void checkUsername(String username) throws DataAccessException;
    void checkGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException;

    public boolean isWatcher(String username, int gameID) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;
    public ChessGame.TeamColor getUserColorFromAuthToken(String authToken, int gameID) throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;
    public void addWatcher(int gameID, String username) throws DataAccessException;

    void checkUser(UserData user) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void authorize(String authToken) throws  DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    void joinGame(GamePlayerData game, String authToken) throws  DataAccessException;
    String userFromAuth(String authToken) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void clear() throws DataAccessException;

}
