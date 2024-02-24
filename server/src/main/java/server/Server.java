package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Handlers handlers = new Handlers();


        //registering the handlers
        Spark.post("/user", handlers::registerUser);

        Spark.post("/session", handlers::login);

        Spark.delete("/session", handlers::logout);

        Spark.get("/game", handlers::listGames);

        Spark.post("/game", handlers::createGame);

        Spark.put("/game", handlers::joinGame);

        Spark.delete("/db", handlers::clear);



        Spark.awaitInitialization();
        return Spark.port();
    }



    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}