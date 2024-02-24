package dataAccess;

import model.UserData;

import java.util.ArrayList;

public class UserDAO {
    ArrayList<UserData> dataArray;

    public UserDAO () {
        dataArray = new ArrayList<>();
    }

    public void clearUsers() {
        dataArray.clear();
    }
}
