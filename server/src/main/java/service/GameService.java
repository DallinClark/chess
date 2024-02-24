package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.GameData;
import model.gameJoinerData;

public class GameService {
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clearGames() throws DataAccessException {
        gameDAO.clearGames();
        userDAO.clearUsers();
        authDAO.clearTokens();
    }

    public int createGame(GameData game) throws DataAccessException{
        int gameID = gameDAO.newGame(game);
        return gameID;
    }

    public GameData[] listGames() throws DataAccessException {
        return gameDAO.getGames();
    }

    public void joinGame(gameJoinerData game) throws DataAccessException {
        gameDAO.joinGame(game.clientColor(),game.gameID(),userDAO.getMyName());
    }

    // Other methods of GameService that use gameDAO, userDAO, and authDAO
}
