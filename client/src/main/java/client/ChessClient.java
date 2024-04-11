package client;


import chess.ChessGame;
import client.WebSocket.NotificationHandler;
import client.WebSocket.WebSocketFacade;
import model.GamePlayerData;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import model.GameData;
import model.GameList;
import server.ServerFacade;
import ui.PrintBoard;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class ChessClient {
    private final ServerFacade serverFacade;
    private boolean isLoggedIn = false;
    private boolean isPlayer;
    GameList currList;
    String color;
    private final NotificationHandler notificationHandler;
    private final String serverUrl;
    private WebSocketFacade ws;
    private boolean inGame = false;
    private String username;
    private int gameID;
    private AuthData playerAuth;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.playerAuth = new AuthData(null,null);
        this.notificationHandler = notificationHandler;
        this.serverUrl = serverUrl;
        this.gameID = -1;
        this.color = null;
    }

    public String eval(String input) {
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length == 0) {
            return "No command entered.";
        }

        String command = tokens[0].toLowerCase();
        try {
            if (!inGame) {
                return switch (command) {
                    case "register" -> register(tokens);
                    case "login" -> login(tokens);
                    case "logout" -> logout();
                    case "create" -> createGame(tokens);
                    case "join" -> joinGame(tokens);
                    case "list" -> listGames(tokens);
                    case "quit" -> "quit";
                    default -> help();
                };
            }
            else {
                return switch (command) {
                    case "redraw" -> redrawBoard();
                    case "leave" -> leaveGame();
                    case "move" -> makeMove(tokens);
                    case "resign" -> resign();
                    case "highlight" -> highlightMoves(tokens);
                    default -> help() ;
                };
            }
        } catch (Exception e) {
            return "Error processing command: " + e.getMessage();
        }
    }

    private String leaveGame() throws ResponseException {
        ws.leaveGame(username, playerAuth.authToken(), gameID, isPlayer);
        gameID = -1;
        inGame = false;
        ws = null;
        return "Left Game Successfully";
    }

    private String redrawBoard() throws ResponseException {
        ws.redrawBoard(gameID, playerAuth.authToken(), this.color);
        return "";
    }

    private String makeMove(String[] tokens) throws ResponseException {
        if (isPlayer) {
            ws.makeMove(tokens[1], tokens[2], color, gameID, tokens[3], username, playerAuth.authToken());
        }
        else {
            return "\n Can't move, you are not a player";
        }
        return "";
    }

    private String resign() throws ResponseException {
        if (!isPlayer) {
            return "Can't resign, you aren't a player";
        }
        ws.resign(gameID, username, color);
        return "";
    }

    private String highlightMoves(String[] tokens) throws ResponseException {
        ws.highlight(gameID, playerAuth.authToken(), tokens[1]);
        return "";
    }

    private String register(String[] tokens) {
        if (tokens.length < 3) return "Usage: register <username> <password> <email>";
        UserData user = new UserData(tokens[1], tokens[2], tokens[3]);
        try {
            playerAuth = serverFacade.registerUser(user);
            playerAuth = serverFacade.login(user);
            this.isLoggedIn = true;
            this.username = tokens[1];
            return "Registration successful, you're now logged in.";
        } catch (IOException | ResponseException e) {
            return "Registration failed: " + e.getMessage();
        }
    }

    private String login(String[] tokens) {
        if (tokens.length < 3) return "Usage: login <username> <password>";
        UserData user = new UserData(tokens[1], tokens[2], null);
        try {
            playerAuth = serverFacade.login(user);
            this.isLoggedIn = true;
            this.username = tokens[1];
            return "Login successful. Welcome, " + username;
        } catch (IOException | ResponseException e) {
            return "Login failed: " + e.getMessage();
        }
    }

    private String logout() {
        if (!isLoggedIn) return "You are not logged in.";
        try {
            serverFacade.logout(playerAuth.authToken());
            this.isLoggedIn = false;
            this.username = null;
            return "Logout successful.";
        } catch (IOException | ResponseException e) {
            return "Logout failed: " + e.getMessage();
        }
    }


    private String createGame(String... params)  {
        try {
            if (!isLoggedIn) {
                return "Please login first.";
            }
            GameData gameData = new GameData();
            gameData.setGameName(params[1]);
            serverFacade.createGame(gameData, playerAuth.authToken());
            return "Game created successfully";
        } catch (IOException | ResponseException e) {
            return "Game Creation failed: " + e.getMessage();
        }
    }

    private String joinGame(String[] tokens) {
        try {
            if (!isLoggedIn) {
                return "Please login first.";
            }
            inGame = true;
            gameID = currList.getIdFromIndex(parseInt(tokens[2]));
            GamePlayerData gameData = new GamePlayerData(tokens[1], gameID);
            serverFacade.joinGame(gameData, playerAuth.authToken());
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            if (tokens[1].equals("WHITE") || tokens[1].equals("BLACK")) {
                ws.joinGamePlayer(username, playerAuth.authToken(), tokens[1], gameID);
                isPlayer = true;
            }
            else {
                ws.joinGameObserver(username, playerAuth.authToken(), gameID);
                isPlayer = false;
            }
            if (tokens[1].equals("BLACK")) {
                this.color = "BLACK";
            }
            else if (tokens[1].equals("WHITE")) {
                this.color = "WHITE";
            }
            this.inGame = true;
            return "Joined game successfully.";
        } catch (IOException | ResponseException e) {
            return "Game Join failed: " + e.getMessage();
        }
    }

    private String listGames(String[] tokens) {
        try {
            if (!isLoggedIn) {
                return "Please login first.";
            }
            currList = serverFacade.listGames(playerAuth.authToken());
            return "List of games: \n" + currList.toString();
        } catch (IOException | ResponseException e) {
            return "Game List failed: " + e.getMessage();
        }
    }

    public String help() {
        if (inGame) {
            return """
                    Here are your options
                    Redraw
                    Leave
                    Move <old spot> <new spot> <promotion piece (null if not applicable)>
                    Resign
                    Highlight <piece position>
                    """;
        }
        if (!isLoggedIn) {
            return """
                    Here are your options
                    Login <username> <password>
                    Register <username> <password> <email>
                    List
                    """;
        }
        return """
                Here are your options
                - List
                - Logout
                - Join <Color (null if observer)> <GameNumber>
                - Create <GameName>
                """;
    }
}

