package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    ArrayList<AuthData> currTokens = new ArrayList<>();
    ArrayList<GameData> games = new ArrayList<>();
    ArrayList<UserData> users = new ArrayList<>();
    int nextID;
    public MemoryDataAccess() {
        currTokens = null;
        users = null;
        games = null;
        nextID = 1;

    }

    @Override
    public void checkUsername(String username) throws DataAccessException {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                throw new DataAccessException(403, "Error: already taken");
            }
        }
    }
    @Override
    public void checkUser(UserData user) throws DataAccessException {
        for (UserData checkUser : users) {
            if (checkUser.username().equals(user.username())) {
                if (checkUser.password().equals(user.password())) {
                    return;
                }
                else {
                    throw new DataAccessException(401, "Error: unauthorized");
                }
            }
        }
        throw new DataAccessException(500, "Error: description");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        for (int i = 0; i < currTokens.size(); ++i) {
            if (currTokens.get(i).authToken().equals(authToken)) {
                currTokens.remove(i);
                return;
            }
        }
        throw new DataAccessException(401, "Error: unauthorized");
    }

    @Override
    public void authorize(String authToken) throws DataAccessException {
        for (AuthData token : currTokens) {
            if (token.authToken().equals(authToken)) {
                return;
            }
        }
        throw new DataAccessException(401, "Error: unauthorized");
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        GameData newGame = new GameData();
        newGame.setGameName(gameName);
        newGame.setGameID(++nextID);
        newGame.setGame(new ChessGame());
        games.add(newGame);
        return nextID;
    }

    @Override
    public void joinGame(GamePlayerData game, String authToken) throws DataAccessException {
        for (GameData checkGame : games) {
            if (checkGame.getGameID() == game.gameID()) {
                if (game.playerColor().equals("WHITE")) {
                    if (checkGame.getWhiteUsername().isEmpty()) {
                        checkGame.setWhiteUsername(this.userFromAuth(authToken));
                    }
                    else {
                        throw new DataAccessException(403, "Error: already taken");
                    }
                }
                else {
                    if (checkGame.getBlackUsername().isEmpty()) {
                        checkGame.setBlackUsername(this.userFromAuth(authToken));
                    }
                    else {
                        throw new DataAccessException(403, "Error: already taken");
                    }
                }
            }
        }
        throw new DataAccessException(500, "Error: description");
    }

    @Override
    public String userFromAuth(String authToken) throws DataAccessException {
        for (AuthData checkToken : currTokens) {
            if (checkToken.authToken().equals(authToken)) {
                return checkToken.username();
            }
        }
        throw new DataAccessException(401, "Error: unauthorized");
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return games.toArray(new GameData[games.size()]);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        currTokens.clear();
        nextID = 1;

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.add(user);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                throw new DataAccessException(400, "Error: bad request");
            }
        }
        UUID uuid = UUID.randomUUID();
        String authToken = uuid.toString();
        AuthData newToken = new AuthData(authToken,username);
        currTokens.add(newToken);
        return newToken;
    }
}
