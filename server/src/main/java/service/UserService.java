package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public UserService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public String registerUser(UserData user) throws DataAccessException {
        if (!userDAO.getUser(user)) {
            String authToken = userDAO.newUser(user);
            authDAO.addData(authToken,user.username());
            return authToken;
        }
        else {
            throw new DataAccessException("user already exists");
        }

    }
}
