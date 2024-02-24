package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{

    ArrayList<GameData> gameArray = new ArrayList<>();

    @Override
    public void clearGames() throws DataAccessException {
        gameArray.clear();
    }
}
