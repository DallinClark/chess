package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket
public class WebSocketHandler {

    public final ConcurrentHashMap<Integer, ConnectionManager> ConnectionManagers = new ConcurrentHashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.type) {
            case JOIN_PLAYER -> joinPlayer(command.getAuthString(),command.getUsername(),command.getMessage(), session, command.gameID);
            case JOIN_OBSERVER -> joinObserver(command.getAuthString(),command.getUsername(), session, command.gameID);
            case LEAVE -> leaveGame(command.getAuthString(), command.getUsername(), session, command.gameID);
        }
    }

    private void joinObserver(String authToken, String username, Session session, int gameID) throws IOException {
        if (ConnectionManagers.get(gameID) != null) {
            ConnectionManagers.get(gameID).add(authToken, session);
        }
        else {
            ConnectionManagers.put(gameID, new ConnectionManager(new ChessGame()));
            ConnectionManagers.get(gameID).add(authToken, session);
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(String.format("%s joined the game as an observer",username));
        ConnectionManagers.get(gameID).broadcast(authToken, notification);
        var gameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        gameNotification.setGame(ConnectionManagers.get(gameID).getGameState());
        ConnectionManagers.get(gameID).singleBroadcast(authToken, gameNotification);
    }
    private void joinPlayer(String authToken,String username, String color, Session session, int gameID) throws IOException {
        if (ConnectionManagers.get(gameID) != null) {
            ConnectionManagers.get(gameID).add(authToken, session);
        }
        else {
            ConnectionManagers.put(gameID, new ConnectionManager(new ChessGame()));
            ConnectionManagers.get(gameID).add(authToken, session);
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(String.format("%s joined the game as %s",username , color));
        ConnectionManagers.get(gameID).broadcast(authToken, notification);
        var gameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        gameNotification.setGame(ConnectionManagers.get(gameID).getGameState());
        gameNotification.setColor(color);
        ConnectionManagers.get(gameID).singleBroadcast(authToken, gameNotification);
    }


    private void leaveGame(String authToken, String username, Session session, int gameID) throws IOException {
        ConnectionManagers.get(gameID).remove(authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(String.format("%s left the game", username));
        ConnectionManagers.get(gameID).broadcast(authToken, notification);
    }

}