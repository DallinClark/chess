package service;

import dataAccess.*;
import model.GameData;

public class GameService {
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String gameName, String authToken) throws DataAccessException{
        dataAccess.authorize(authToken);
        return dataAccess.createGame(gameName);
    }

    public void joinGame(GamePlayerData game, String authToken) throws DataAccessException {
        dataAccess.authorize(authToken);
        dataAccess.joinGame(game, authToken);
    }

    public GameData[] listGames(String authToken) throws DataAccessException{
        dataAccess.authorize(authToken);
        return dataAccess.listGames();
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    // Other methods of GameService that use gameDAO, userDAO, and authDAO
}
