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
    public void addData(AuthData data) throws DataAccessException {
        dataArray.add(data);
    }

    @Override
    public boolean logout(String authToken) throws DataAccessException {
        for (int i = 0; i < dataArray.size(); ++i) {
            if (dataArray.get(i).authToken().equals(authToken)) {
                dataArray.remove(i);
                return true;
            }
        }
        return false;
    }
}
