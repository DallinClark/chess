package dataAccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface UserDAO {
    void clearUsers() throws DataAccessException;

    AuthData newUser(UserData user) throws DataAccessException;

    boolean getUser(UserData user) throws DataAccessException;

    boolean getPassword(UserData user) throws DataAccessException;

    String getMyName() throws DataAccessException;

}