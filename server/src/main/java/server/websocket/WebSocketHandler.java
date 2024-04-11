package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket
public class WebSocketHandler {

    public final ConcurrentHashMap<Integer, ConnectionManager> connectionManagers = new ConcurrentHashMap<>();
    private DataAccess dataAccess;
    private boolean isWatcher;

    public WebSocketHandler(DataAccess newDataAccess) {
        dataAccess = newDataAccess;
        isWatcher = false;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InvalidMoveException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        if (command == null || command.getCommandType() == null) {
            System.err.println("Received null command or command type");
            // Handle the error appropriately. For example, send an error response:
            // session.getRemote().sendString("Error: Invalid command");
            return;
        }
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(command.getAuthString(),command.getUsername(),command.getPlayerColor(), session, command.gameID);
            case JOIN_OBSERVER -> joinObserver(command.getAuthString(),command.getUsername(), session, command.gameID, command.getPlayerColor());
            case LEAVE -> leaveGame(command.getAuthString(), command.getUsername(), session, command.gameID, command.getMessage());
            case REDRAW -> redraw(command.gameID, command.getAuthString(), command.getMessage());
            case MAKE_MOVE -> makeMove(command.gameID, command.getPlayerColor(), command.getOldMove(), command.getNewMove(), command.getPromotionPiece(), command.getUsername(), command.getAuthString(), command.getMove());
            case RESIGN -> resign(command.gameID,command.getAuthString(), command.getPlayerColor());
            case HIGHLIGHT -> highlight(command.gameID, command.getAuthString(), command.getMessage());
            default -> {return;}
        }
    }

    private void highlight(int gameID, String authToken, String position) throws IOException {
        int col = Character.toUpperCase(position.charAt(0)) - 'A' + 1;
        int row = Character.getNumericValue(position.charAt(1));
        ChessPosition highlightPosition = new ChessPosition(row, col);
        connectionManagers.get(gameID).highlightMoves(authToken, highlightPosition);
    }


    private void resign(int gameId, String authToken, ChessGame.TeamColor color) throws IOException, DataAccessException {
        try {
            if (connectionManagers.get(gameId).isGameOver()) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                notification.setErrorMessage("The game is over");
                connectionManagers.get(gameId).singleBroadcast(authToken, notification);
                return;
            }
            String username = dataAccess.userFromAuth(authToken);
            if (connectionManagers.get(gameId).isWatcher(authToken)) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                notification.setErrorMessage("You're an observer :(");
                connectionManagers.get(gameId).singleBroadcast(authToken, notification);
                return;
            }
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            if (color == null) {
                notification.setMessage(String.format("%s resigned!", username));
            } else {
                notification.setMessage(String.format("%s resigned, %s wins!", username, color.toString()));
            }
            connectionManagers.get(gameId).broadcast(null, notification);
            endGame(gameId);
        } catch (DataAccessException e){
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("You're an observer :(");
            connectionManagers.get(gameId).singleBroadcast(authToken, notification);
        }
    }
    private void makeMove(int gameId, ChessGame.TeamColor color, String oldMove, String newMove, String promotionPiece, String username, String authToken, ChessMove tryMove) throws IOException {
        try {
            ChessMove move;
            if (tryMove != null) {
                move = tryMove;
            }
            else {
                move = createMove(oldMove, newMove, promotionPiece);
            }
            if (connectionManagers.get(gameId).checkColor(color)) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                notification.setErrorMessage("Not your turn");
                connectionManagers.get(gameId).singleBroadcast(authToken, notification);
                return;
            }
            if (connectionManagers.get(gameId).checkTurn() !=  dataAccess.getUserColorFromAuthToken(authToken, gameId)) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                notification.setErrorMessage("Not your turn");
                connectionManagers.get(gameId).singleBroadcast(authToken, notification);
                return;
            }
            dataAccess.isWatcher(username, gameId);
            if (!connectionManagers.get(gameId).makeMove(move, authToken)) {
                return;
            }
            var gameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            gameNotification.setGame(connectionManagers.get(gameId).getGameState());
            connectionManagers.get(gameId).broadcast(null, gameNotification);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(String.format("%s made the move %s to %s", username, oldMove, newMove));
            connectionManagers.get(gameId).broadcast(authToken, notification);
        } catch(InvalidMoveException exception) {
            if (exception.getMessage().equals("Invalid Move")) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                notification.setErrorMessage("Invalid move");
                connectionManagers.get(gameId).singleBroadcast(authToken, notification);
            }
            else {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(String.format("%s made the move %s to %s", username, oldMove, newMove));
                connectionManagers.get(gameId).broadcast(null, notification);
                var gameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                gameNotification.setGame(connectionManagers.get(gameId).getGameState());
                connectionManagers.get(gameId).broadcast(null, gameNotification);
                var overNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                overNotification.setMessage(exception.getMessage());
                connectionManagers.get(gameId).broadcast(null, overNotification);
                endGame(gameId);
            }
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.setErrorMessage("You are an observer :(");
            connectionManagers.get(gameId).singleBroadcast(authToken, notification);
        }

    }
    private void redraw(int gameID, String authToken, String color) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        notification.setGame(connectionManagers.get(gameID).getGameState());
        notification.setColor(color);
        connectionManagers.get(gameID).singleBroadcast(authToken, notification);
    }
    private void joinObserver(String authToken, String username, Session session, int gameID, ChessGame.TeamColor color) throws IOException {
        try {
            if (connectionManagers.get(gameID) != null) {
                connectionManagers.get(gameID).add(authToken, session);
            } else {
                connectionManagers.put(gameID, new ConnectionManager(new ChessGame()));
                connectionManagers.get(gameID).add(authToken, session);
            }
            dataAccess.authorize(authToken);
            if (username == null) {
                username = dataAccess.userFromAuth(authToken);
            }
            dataAccess.isWatcher(username, gameID);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(String.format("%s joined the game as an observer", username));
            connectionManagers.get(gameID).broadcast(authToken, notification);
            var gameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            gameNotification.setGame(connectionManagers.get(gameID).getGameState());
            connectionManagers.get(gameID).singleBroadcast(authToken, gameNotification);
            connectionManagers.get(gameID).setWatcher(authToken);
        } catch (DataAccessException e) {
        var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        errorNotification.setErrorMessage(e.getMessage());
        connectionManagers.get(gameID).singleBroadcast(authToken, errorNotification);
    }
    }
    private void joinPlayer(String authToken, String username, ChessGame.TeamColor color, Session session, int gameID) throws IOException {
        try {
            if (connectionManagers.get(gameID) != null) {
                connectionManagers.get(gameID).add(authToken, session);
            } else {
                connectionManagers.put(gameID, new ConnectionManager(new ChessGame()));
                connectionManagers.get(gameID).add(authToken, session);
            }
            dataAccess.authorize(authToken);
            if (username == null) {
                username = dataAccess.userFromAuth(authToken);
            }
            dataAccess.checkGame(gameID, username, color);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(String.format("%s joined the game as %s", username, color));
            connectionManagers.get(gameID).broadcast(authToken, notification);
            var gameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            gameNotification.setGame(ConnectionManagers.get(gameID).getGameState());
            gameNotification.setColor(null); //TODO fix this
            connectionManagers.get(gameID).singleBroadcast(authToken, gameNotification);
        } catch (DataAccessException e) {
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorNotification.setErrorMessage(e.getMessage());
            connectionManagers.get(gameID).singleBroadcast(authToken, errorNotification);
        }
    }


    private void leaveGame(String authToken, String username, Session session, int gameID, String player) throws IOException {
        connectionManagers.get(gameID).remove(authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (player == null) {
            if (connectionManagers.get(gameID).isWatcher(authToken)) {
                notification.setMessage(String.format("%s stopped spectating", username));
            }
            else {
                notification.setMessage(String.format("%s left the game, they were a root client, ending game", username));
                endGame(gameID);
            }
        }
        else if (player.equals("player")) {
            notification.setMessage(String.format("%s left the game, they were a root client, ending game", username));
            endGame(gameID);
        }
        else {
            notification.setMessage(String.format("%s stopped spectating", username));
        }
        connectionManagers.get(gameID).broadcast(authToken, notification);
    }

    private void endGame(int gameID) {
        connectionManagers.get(gameID).setGameOver(true);
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