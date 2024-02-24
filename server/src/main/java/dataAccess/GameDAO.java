package dataAccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    GameData[] getGames() throws DataAccessException;

    void clearGames() throws DataAccessException;

    int newGame(GameData game) throws DataAccessException;

    void joinGame(String clientColor, int gameID, String username) throws DataAccessException;
}
