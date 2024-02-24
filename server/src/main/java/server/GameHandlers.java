package server;

import com.google.gson.Gson;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandlers {
    private final GameService gameService;
    private final Gson gson;

    public GameHandlers() {
        this.gameService = new GameService();
        this.gson = new Gson();
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
