package server;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
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

    private final Gson gson;

    public Handlers() {
        this.userService = new UserService(authDAO,gameDAO,userDAO);
        this.authService = new AuthService(authDAO,gameDAO,userDAO);
        this.gameService = new GameService(authDAO,gameDAO,userDAO);
        this.gson = new Gson();
    }

    public String registerUser(Request req, Response res) {
        return "called";
    }
    public String login(Request req, Response res) {
        return "called";
    }
    public String logout(Request req, Response res) {
        return "called";
    }

    public String joinGame(Request req, Response res) {
        return "called";
    }
    public String createGame(Request req, Response res) {
        return "called";
    }
    public String listGames(Request req, Response res) {
        return "called";
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
