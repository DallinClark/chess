package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public ChessGame gameState;

    public ChessGame getGameState() {
        return gameState;
    }

    public void setGameState(ChessGame gameState) {
        this.gameState = gameState;
    }

    public ConnectionManager(ChessGame game) {
        gameState = game;
    }

    public void add(String authToken, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
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

}