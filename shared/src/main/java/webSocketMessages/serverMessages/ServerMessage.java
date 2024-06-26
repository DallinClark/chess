package webSocketMessages.serverMessages;

import java.util.Collection;
import java.util.Objects;
import chess.ChessGame;
import chess.ChessMove;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ChessGame game;
    public String errorMessage;
    public String color;
    public Collection<ChessMove> legalMoves;

    public Collection<ChessMove> getLegalMoves() {
        return legalMoves;
    }

    public void setLegalMoves(Collection<ChessMove> legalMoves) {
        this.legalMoves = legalMoves;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        LOAD_MOVES
    }

    public ServerMessage(ServerMessageType type) {
        this.message = null;
        this.errorMessage = null;
        this.game = null;
        this.serverMessageType = type;
        this.color = null;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage))
            return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
