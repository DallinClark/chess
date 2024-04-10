package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.GamePlayerData;
import model.UserData;
import server.websocket.WebSocketHandler;
import service.*;
import spark.*;
import model.GameList;

public class Server {
    GameService gameService;
    UserService userService;
    private final WebSocketHandler webSocketHandler;


    public Server() {
        DataAccess dataAccess = null;
        try {
            dataAccess = new SqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", webSocketHandler);


        //registering the handlers
        Spark.post("/user", this::registerUser);

        Spark.post("/session", this::login);

        Spark.delete("/session", this::logout);

        Spark.get("/game", this::listGames);

        Spark.post("/game", this::createGame);

        Spark.put("/game", this::joinGame);

        Spark.delete("/db", this::clear);

        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request req, Response res) {
        try {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            AuthData registeredUser = userService.registerUser(user);
            res.status(200);
            return new Gson().toJson(registeredUser);
        } catch (DataAccessException ex) {
            res.status(ex.StatusCode());
            return new Gson().toJson(new ErrorResponse(ex.getMessage()));
        }

    }

    private Object login(Request req, Response res)  {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authToken = userService.login(user);
            return new Gson().toJson(authToken);
        } catch (DataAccessException ex) {
            res.status(ex.StatusCode());
            return new Gson().toJson(new ErrorResponse(ex.getMessage()));
        }
    }

    private Object logout(Request req, Response res) {
        try {
            var authToken = req.headers("Authorization");
            userService.logout(authToken);
            res.status(200);
            return "";
        } catch (DataAccessException ex) {
            res.status(ex.StatusCode());
            return new Gson().toJson(new ErrorResponse(ex.getMessage()));

        }
    }

    private Object createGame(Request req, Response res) {
        try {
            var game = new Gson().fromJson(req.body(), GameData.class);
            String authToken = req.headers("Authorization");
            int gameID = gameService.createGame(game.getGameName(), authToken);
            GameData newGame = new GameData();
            newGame.setGameID(gameID);
            res.status(200);
            return new Gson().toJson(newGame);
        } catch (DataAccessException ex) {
            res.status(ex.StatusCode());
            return new Gson().toJson(new ErrorResponse(ex.getMessage()));

        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            var game = new Gson().fromJson(req.body(), GamePlayerData.class);
            String authToken = req.headers("Authorization");
            gameService.joinGame(game, authToken);
            res.status(200);
            return "";
        } catch (DataAccessException ex) {
            res.status(ex.StatusCode());
            return new Gson().toJson(new ErrorResponse(ex.getMessage()));

        }

    }

    private Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            GameData[] gamesList = gameService.listGames(authToken);
            GameList games = new GameList(gamesList);
            res.status(200);
            return new Gson().toJson(games);
        } catch (DataAccessException ex) {
            res.status(ex.StatusCode());
            return new Gson().toJson(new ErrorResponse(ex.getMessage()));

        }
    }
    private Object clear(Request req, Response res) {
        try {
            gameService.clear();
            res.status(200);
            return "";
        } catch (DataAccessException ex) {
            res.status(ex.StatusCode());
            return new Gson().toJson(new ErrorResponse(ex.getMessage()));

        }
    }
    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    class ErrorResponse {
        private String message;

        public ErrorResponse(String errorMessage) {
            this.message = errorMessage;
        }
    }


}