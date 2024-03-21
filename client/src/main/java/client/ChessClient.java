package client;


import chess.ChessGame;
import client.WebSocket.NotificationHandler;
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

    GameList currList;

    PrintBoard boardPrinter;

    private boolean inGame = false;
    private String username;

    private AuthData playerAuth;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.playerAuth = new AuthData(null,null);
    }

    public String eval(String input) {
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length == 0) {
            return "No command entered.";
        }

        String command = tokens[0].toLowerCase();
        try {
            return switch (command) {
                case "register" -> register(tokens);
                case "login" -> login(tokens);
                case "logout"-> logout();
                case "create" -> createGame(tokens);
                case "join" -> joinGame(tokens);
                case "list" -> listGames(tokens);
                case "quit" ->"quit";
                default -> help();
            };
        } catch (Exception e) {
            return "Error processing command: " + e.getMessage();
        }
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
            int gameId = currList.getIdFromIndex(parseInt(tokens[2]));
            GamePlayerData gameData = new GamePlayerData(tokens[1], gameId);
            serverFacade.joinGame(gameData, playerAuth.authToken());
            ChessGame game = (ChessGame) currList.getGameFromIndex(parseInt(tokens[2]));
            PrintBoard.printGameBoard(game.getBoard());
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
                - Join <Color> <GameNumber>
                - Create <GameName>
                """;
    }
}

