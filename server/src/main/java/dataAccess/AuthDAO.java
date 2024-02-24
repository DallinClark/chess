package dataAccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    void clearTokens() throws DataAccessException;

    void addData(AuthData data) throws DataAccessException;

    boolean logout(String authToken) throws DataAccessException;

}
