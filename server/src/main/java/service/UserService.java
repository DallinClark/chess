package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public AuthData registerUser(UserData user) throws DataAccessException {
        dataAccess.checkUsername(user.username());
        dataAccess.createUser(user);
        return dataAccess.createAuth(user.username());
    }

    public AuthData login(UserData user) throws DataAccessException {
        dataAccess.checkUser(user);
        return dataAccess.createAuth(user.username());
    }

    public void logout(String authToken) throws DataAccessException {
        dataAccess.deleteAuth(authToken);
    }
}
