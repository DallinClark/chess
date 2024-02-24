package dataAccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{

    ArrayList<AuthData> dataArray = new ArrayList<>();

    @Override
    public void clearTokens() throws DataAccessException {
        dataArray.clear();
    }

    @Override
    public void addData(String authToken, String username) throws DataAccessException {
        AuthData data = new AuthData(authToken, username);
        dataArray.add(data);
    }
}
