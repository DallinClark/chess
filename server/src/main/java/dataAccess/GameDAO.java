package dataAccess;

import model.GameData;

import java.util.ArrayList;

public class GameDAO {
    ArrayList<GameData> dataArray;

    public GameDAO () {
        dataArray = new ArrayList<>();
    }

    public void clearGames() {
        dataArray.clear();
    }
}
