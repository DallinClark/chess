package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO{

    ArrayList<GameData> gameArray = new ArrayList<>();
    int gameIDs = 0;

    @Override
    public GameData[] getGames() throws DataAccessException {
        List<GameData> gamesList = new ArrayList<>();
        for (GameData game : gameArray) {
            gamesList.add(game);
        }
        // Convert the List back to an array before returning
        GameData[] games = new GameData[gamesList.size()];
        return gamesList.toArray(games);
    }

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

    @Override
    public void joinGame(String clientColor, int gameID, String username) throws DataAccessException {
        for (GameData game : gameArray) {
            if (game.getGameID() == gameID) {
                if (clientColor.equals("WHITE")) {
                    game.setWhiteUsername(username);
                }
                else {
                    game.setBlackUsername(username);
                }
            }
        }
        throw new DataAccessException("game not found");
    }
}
