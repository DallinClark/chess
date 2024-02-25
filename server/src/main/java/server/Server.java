package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.GamePlayerData;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.*;
import spark.*;

public class Server {
    GameService gameService;
    UserService userService;


    public Server() {
        DataAccess dataAccess = null;
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        //registering the handlers
        Spark.post("/user", this::registerUser);

        Spark.post("/session", this::login);

        Spark.delete("/session", this::logout);

        Spark.get("/game", this::listGames);

        Spark.post("/game", this::createGame);

        Spark.put("/game", this::joinGame);

        Spark.delete("/db", this::clear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public String registerUser(Request req, Response res) throws DataAccessException{
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData registeredUser = userService.registerUser(user);
        return new Gson().toJson(registeredUser);
    }

    public String login(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        AuthData authToken = userService.login(user);
        return new Gson().toJson(authToken);
    }

    public Object logout(Request req, Response res) throws DataAccessException{
        var authToken = req.headers("Authorization");
        userService.logout(authToken);
        res.status(200);
        return "";
    }

    public String createGame(Request req, Response res) throws DataAccessException{
        var game = new Gson().fromJson(req.body(), GameData.class);
        String authToken = req.headers("Authorization");
        int gameID = gameService.createGame(game.getGameName(), authToken);
        res.status(200);
        return new Gson().toJson(gameID);
    }

    public Object joinGame(Request req, Response res) throws DataAccessException {

        var game = new Gson().fromJson(req.body(), GamePlayerData.class);
        String authToken = req.headers("authToken");
        gameService.joinGame(game, authToken);
        res.status(200);
        return "";

    }

    public String listGames(Request req, Response res) throws DataAccessException{

        String authToken = req.headers("authToken");
        GameData[] games = gameService.listGames(authToken);
        return new Gson().toJson(games);
    }
    public Object clear(Request req, Response res) throws DataAccessException{
        gameService.clear();
        res.status(200);
        return "";

    }
    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }


}