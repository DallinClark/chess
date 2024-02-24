package server;

import com.google.gson.Gson;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandlers {
    private final UserService userService; // Assume UserService is a class that handles business logic.
    private final Gson gson = new Gson();

    public UserHandlers(UserService userService) {
        this.userService = userService;
    }

    public String createUser(Request req, Response res) {
        return "called";
    }
    }
