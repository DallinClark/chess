package server;

import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserService userService = new UserService();
        UserHandlers userHandler = new UserHandlers(userService);



        // Register your endpoints and handle exceptions here.
        registerEndpoints();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void registerEndpoints() {
        Spark.delete("/db", (req, res) -> {
            // Logic to clear the database
            return "Database cleared";
        });

        // Register
        Spark.post("/user", (req, res) -> {
            // Logic to register a new user
            return "User registered";
        });

        // Login
        Spark.post("/session", (req, res) -> {
            // Logic to log in a user
            return "User logged in";
        });

        // Logout
        Spark.delete("/session", (req, res) -> {
            // Logic to log out a user
            return "User logged out";
        });

        // List Games
        Spark.get("/game", (req, res) -> {
            // Logic to list all games
            return "List of games";
        });

        // Create Game
        Spark.post("/game", (req, res) -> {
            // Logic to create a new game
            return "Game created";
        });

        // Join Game
        Spark.put("/game", (req, res) -> {
            // Logic for a user to join a game
            return "Joined game";
        });
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}