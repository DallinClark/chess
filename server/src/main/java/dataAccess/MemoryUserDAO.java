package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class MemoryUserDAO implements UserDAO{

    ArrayList<UserData> userArray = new ArrayList<>();
    String myName = "";

    @Override
    public void clearUsers() throws DataAccessException {
        userArray.clear();
        myName = "";
    }

    @Override
    public AuthData newUser(UserData user) throws DataAccessException {
        userArray.add(user);
        UUID uuid = UUID.randomUUID();
        String authToken = uuid.toString();
        myName = user.username();
        return new AuthData(authToken,user.username());
    }

    @Override
    public boolean getUser(UserData user) throws DataAccessException {
        for (UserData compareUser : userArray) {
            if (compareUser.username().equals(user.username())) {
                return true;
            }
        }
        return false;
    }


    public boolean getPassword(UserData user) throws DataAccessException {
        for (UserData compareUser : userArray) {
            if (compareUser.username().equals(user.username()) & compareUser.password().equals(user.password())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getMyName() throws DataAccessException {
        return myName;
    }
}
