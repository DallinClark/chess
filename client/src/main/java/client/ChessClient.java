package client;


import client.WebSocket.NotificationHandler;
import model.GamePlayerData;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import model.GameData;
import model.GameList;
import server.ServerFacade;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class ChessClient {
    private final ServerFacade serverFacade;
    private boolean isLoggedIn = false;
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
            return "Registration successful.";
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
            GameData game = serverFacade.createGame(gameData, playerAuth.authToken());
            return "Game created successfully, GameID: " + game.getGameID();
        } catch (IOException | ResponseException e) {
            return "Game Creation failed: " + e.getMessage();
        }
    }

    private String joinGame(String[] tokens) {
        try {
            if (!isLoggedIn) {
                return "Please login first.";
            }
            GamePlayerData gameData = new GamePlayerData(tokens[1], parseInt(tokens[2]));
            serverFacade.joinGame(gameData, playerAuth.authToken());
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
            GameList list = serverFacade.listGames(playerAuth.authToken());
            return "List of games: " + list.toString();
        } catch (IOException | ResponseException e) {
            return "Game List failed: " + e.getMessage();
        }
    }

    public String help() {
        if (!isLoggedIn) {
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }
}

