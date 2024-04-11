package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public ChessGame gameState;
    public boolean gameOver;

    public ChessGame getGameState() {
        return gameState;
    }

    public void setGameState(ChessGame gameState) {
        this.gameState = gameState;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public ConnectionManager(ChessGame game) {
        gameState = game;
        gameOver = false;
    }

    public void add(String authToken, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }
    public boolean makeMove(ChessMove move, String authToken) throws InvalidMoveException, IOException {
        if (gameOver) {
            var overNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            overNotification.setErrorMessage("Can't move, game is over");
            singleBroadcast(authToken, overNotification);
            return false;
        }
        gameState.makeMove(move);
        return true;
    }

    public void broadcast(String excludeToken, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeToken)) {
                    c.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void singleBroadcast(String authToken, ServerMessage message) throws IOException {
        connections.get(authToken).send(new Gson().toJson(message));
    }

    public void endGame() {
        for (var c : connections.values()) {
            connections.remove(c.authToken);
        }
    }

    public void highlightMoves(String authToken, ChessPosition position) throws IOException {
        Collection<ChessMove> moves = gameState.validMoves(position);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_MOVES);
        notification.setLegalMoves(moves);
        notification.setGame(gameState);
        singleBroadcast(authToken, notification);
    }

    public boolean checkColor(ChessGame.TeamColor color) {
        return gameState.getTeamTurn().equals(color);
    }

    public ChessGame.TeamColor checkTurn() {
        return gameState.getTeamTurn();
    }

    public void setWatcher(String authToken) {
        connections.get(authToken).setWatcher(true);
    }
    public boolean isWatcher(String authToken) {
        if (connections.containsKey(authToken)) {
            return connections.get(authToken).isWatcher();
        } else {
            return false; // authToken is not a key in the connections map
        }
    }
}