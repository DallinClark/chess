package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
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
    public void makeMove(ChessMove move) throws InvalidMoveException {
        gameState.makeMove(move);
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

}