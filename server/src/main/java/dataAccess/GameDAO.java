package dataAccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clearGames() throws DataAccessException;

    int newGame(GameData game) throws DataAccessException;
}
