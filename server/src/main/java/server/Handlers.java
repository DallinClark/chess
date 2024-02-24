package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.gameJoinerData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class Handlers {
    private final UserService userService; // Assume UserService is a class that handles business logic.
    private final GameService gameService;
    private final AuthService authService;
    public AuthDAO authDAO;
    public GameDAO gameDAO;
    public UserDAO userDAO;

    public Handlers() {
        this.userService = new UserService(authDAO,gameDAO,userDAO);
        this.authService = new AuthService(authDAO,gameDAO,userDAO);
        this.gameService = new GameService(authDAO,gameDAO,userDAO);
    }

    public String registerUser(Request req, Response res)  {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authToken = userService.registerUser(user);
            return new Gson().toJson(authToken);
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return "{\"message\": \"Error: Internal Server Error\"}";
        }
    }
    public String login(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authToken = userService.login(user);
            return new Gson().toJson(authToken);
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return "{\"message\": \"Error: Internal Server Error\"}";
        }
    }
    public String logout(Request req, Response res) {
        try {
            var authToken = new Gson().fromJson(req.body(), String.class);
            authService.logout(authToken);
            res.status(200);
            return null;
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return "{\"message\": \"Error: Internal Server Error\"}";
        }
    }

    public String joinGame(Request req, Response res) {
        try {
            var game = new Gson().fromJson(req.body(), gameJoinerData.class);
            String authToken = req.headers("authToken");
            authService.authenticate(authToken);
            gameService.joinGame(game);
            res.status(200);
            return null;
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return "{\"message\": \"Error: Internal Server Error\"}";
        }
    }
    public String createGame(Request req, Response res) {
        try {
            var gameName = new Gson().fromJson(req.body(), GameData.class);
            String authToken = req.headers("authToken");
            authService.authenticate(authToken);
            int gameID = gameService.createGame(gameName);
            res.status(200);
            return new Gson().toJson(gameID);
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return "{\"message\": \"Error: Internal Server Error\"}";
        }
    }
    public String listGames(Request req, Response res) {
        try {
            String authToken = req.headers("authToken");
            authService.authenticate(authToken);
            GameData[] games = gameService.listGames();
            res.status(200);
            return new Gson().toJson(games);
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return "{\"message\": \"Error: Internal Server Error\"}";
        }
    }
    public String clear(Request req, Response res) {
        try {
            gameService.clearGames();
            res.status(200);
            return res.body(); // Assuming the request body contains user registration data
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return "{\"message\": \"Error: Internal Server Error\"}";
        }
    }

}
