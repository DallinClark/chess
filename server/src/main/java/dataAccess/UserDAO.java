package dataAccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface UserDAO {
    void clearUsers() throws DataAccessException;

    String newUser(UserData user) throws DataAccessException;

    boolean getUser(UserData user) throws DataAccessException;

}