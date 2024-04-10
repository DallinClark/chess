package server.websocket;

import chess.*;
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
    public void onMessage(Session session, String message) throws IOException, InvalidMoveException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.type) {
            case JOIN_PLAYER -> joinPlayer(command.getAuthString(),command.getUsername(),command.getMessage(), session, command.gameID);
            case JOIN_OBSERVER -> joinObserver(command.getAuthString(),command.getUsername(), session, command.gameID);
            case LEAVE -> leaveGame(command.getAuthString(), command.getUsername(), session, command.gameID, command.getMessage());
            case REDRAW -> redraw(command.gameID, command.getAuthString(), command.getMessage());
            case MAKE_MOVE -> makeMove(command.gameID, command.getMessage(), command.getOldMove(), command.getNewMove(), command.getPromotionPiece(), command.getUsername(), command.getAuthString());
        }
    }

    private void makeMove(int gameId, String color, String oldMove, String newMove, String promotionPiece, String username, String authToken) throws IOException {
        try {
            ChessMove move = createMove(oldMove, newMove, promotionPiece);
            ConnectionManagers.get(gameId).makeMove(move);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(String.format("%s made the move %s to %s", username, oldMove, newMove));
            ConnectionManagers.get(gameId).broadcast(null, notification);
            var gameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            gameNotification.setGame(ConnectionManagers.get(gameId).getGameState());
            ConnectionManagers.get(gameId).broadcast(null, gameNotification);
        } catch(InvalidMoveException exception) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage("Invalid move");
            ConnectionManagers.get(gameId).singleBroadcast(authToken, notification);
        }

    }
    private void redraw(int gameID, String authToken, String color) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        notification.setGame(ConnectionManagers.get(gameID).getGameState());
        notification.setColor(color);
        ConnectionManagers.get(gameID).singleBroadcast(authToken, notification);
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


    private void leaveGame(String authToken, String username, Session session, int gameID, String player) throws IOException {
        ConnectionManagers.get(gameID).remove(authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (player.equals("player")) {
            notification.setMessage(String.format("%s left the game, they were a root client, ending game", username));
            endGame(gameID);
        }
        else {
            notification.setMessage(String.format("%s stopped spectating", username));
        }
        ConnectionManagers.get(gameID).broadcast(authToken, notification);
    }

    private void endGame(int gameID) {
        ConnectionManagers.get(gameID).setGameOver(false);
    }

    private ChessMove createMove(String oldMove, String newMove, String promotionPiece)  {
        char c = Character.toUpperCase(oldMove.charAt(0));
        int oldColumn = c - 'A' + 1;
        int oldRow = Character.getNumericValue(oldMove.charAt(1));
        c = Character.toUpperCase(newMove.charAt(0));
        int newColumn = c - 'A' + 1;
        int newRow = Character.getNumericValue(newMove.charAt(1));

        return new ChessMove(new ChessPosition(oldRow, oldColumn), new ChessPosition(newRow, newColumn), getPieceTypeFromString(promotionPiece));

    }

    public ChessPiece.PieceType getPieceTypeFromString(String pieceName) {
        if (pieceName == null) {
            return null;
        }
        try {
            return ChessPiece.PieceType.valueOf(pieceName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


}