package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class AuthService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authDAO.logout(authToken)) {
            return;
        }
        else {
            throw new DataAccessException("couldn't find token");
        }
    }

    // Other methods of AuthService that use authDAO, gameDAO, and userDAO
}