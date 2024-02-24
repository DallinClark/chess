package dataAccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    void clearTokens() throws DataAccessException;

    void addData(String authToken, String username) throws DataAccessException;

}
