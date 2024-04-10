package client.WebSocket;

import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage newMessage = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(newMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void makeMove(String oldPos, String newPos, String color, int gameID, String promotionPiece, String username, String authToken) throws ResponseException {
        try {
            var action = new UserGameCommand(authToken, username, UserGameCommand.CommandType.MAKE_MOVE, color, gameID);
            action.setOldMove(oldPos);
            action.setNewMove(newPos);
            action.setPromotionPiece(promotionPiece);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }

    }

    public void redrawBoard(int gameID, String authToken, String color) throws ResponseException {
        try {
            var action = new UserGameCommand(authToken, null, UserGameCommand.CommandType.REDRAW, color, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinGamePlayer(String username, String authToken, String color, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(authToken, username, UserGameCommand.CommandType.JOIN_PLAYER, color, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinGameObserver(String username, String authToken, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(authToken, username, UserGameCommand.CommandType.JOIN_OBSERVER, null, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(String username, String authToken,int gameID, boolean isPlayer) throws ResponseException {
        try {
            String message;
            if (isPlayer) {
                message = "player";
            }
            else {
                message = "observer";
            }
            var action = new UserGameCommand(authToken, username, UserGameCommand.CommandType.LEAVE, message, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}