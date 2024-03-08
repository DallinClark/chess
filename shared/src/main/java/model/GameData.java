package model;
import chess.*;

public class GameData {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;

    // Constructor
    public GameData() {
        this.gameID = 0;
        this.whiteUsername = "";
        this.blackUsername = "";
        this.gameName = "";
        this.game = null;
    }

    // Getters
    public int getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getGameName() {
        return gameName;
    }



    // Setters
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public Object getGame() {
        return this.game;
    }
}


