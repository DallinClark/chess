package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{

    ArrayList<GameData> gameArray = new ArrayList<>();
    int gameIDs = 0;

    @Override
    public void clearGames() throws DataAccessException {
        gameArray.clear();
    }

    @Override
    public int newGame(GameData game) throws DataAccessException {
        game.setGameID(gameIDs);
        gameIDs += 1;
        return game.getGameID();
    }
}
