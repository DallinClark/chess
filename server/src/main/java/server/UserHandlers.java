package server;

import com.google.gson.Gson;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandlers {
    private final UserService userService; // Assume UserService is a class that handles business logic.
    private final Gson gson;

    public UserHandlers() {
        this.userService = new UserService();
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
}
