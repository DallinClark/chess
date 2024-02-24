package server;

import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserHandlers userHandler = new UserHandlers();
        GameHandlers gameHandler = new GameHandlers();


        //registering the handlers
        Spark.post("/user", userHandler::registerUser);

        Spark.post("/session", userHandler::login);

        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);

        Spark.post("/game", gameHandler::createGame);

        Spark.put("/game", gameHandler::joinGame);

        Spark.delete("/db", gameHandler::clear);


        Spark.awaitInitialization();
        return Spark.port();
    }



    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}