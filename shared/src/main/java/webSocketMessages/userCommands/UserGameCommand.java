package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public String getUsername() {
        return username;
    }

    public CommandType getType() {
        return commandType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserGameCommand(String authToken, String username, CommandType type, String message, int gameID) {
        this.authToken = authToken;
        this.username = username;
        this.commandType = type;
        this.message = message;
        this.gameID = gameID;
        this.oldMove = null;
        this.newMove = null;
        this.move = null;
    }

    public String getOldMove() {
        return oldMove;
    }

    public void setOldMove(String oldMove) {
        this.oldMove = oldMove;
    }

    public String getNewMove() {
        return newMove;
    }

    public void setNewMove(String newMove) {
        this.newMove = newMove;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN,
        REDRAW,
        HIGHLIGHT

    }

    protected CommandType commandType;

    private final String authToken;
    public String username;
    public ChessGame.TeamColor playerColor;

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(String promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

    public String oldMove;
    public ChessMove move;

    public ChessMove getMove() {
        return move;
    }

    public String newMove;
    public String promotionPiece;
    public String message;
    public int gameID;
    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}
