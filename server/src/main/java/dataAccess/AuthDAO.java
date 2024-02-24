package dataAccess;

import model.AuthData;

import java.util.ArrayList;

public class AuthDAO {
    ArrayList<AuthData> dataArray;

    public AuthDAO () {
        dataArray = new ArrayList<>();
    }

    public void clearTokens() {
        dataArray.clear();
    }
}
