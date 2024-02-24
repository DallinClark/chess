package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
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

    public AuthData registerUser(UserData user) throws DataAccessException {
        if (!userDAO.getUser(user)) {
            AuthData data = userDAO.newUser(user);
            authDAO.addData(data);
            return data;
        }
        else {
            throw new DataAccessException("user already exists");
        }
    }

    public AuthData login(UserData user) throws DataAccessException {
        if (userDAO.getUser(user)) {
            if (userDAO.getPassword(user)) {
                AuthData data = userDAO.newUser(user);
                authDAO.addData(data);
                return data;
            }
            else {
                throw new DataAccessException("wrong password");
            }
        }
        else {
            throw new DataAccessException("user doesn't exist");
        }
    }
}
