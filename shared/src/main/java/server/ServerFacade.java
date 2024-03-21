package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GamePlayerData;
import model.UserData;
import exception.ResponseException;
import model.GameList;


import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData registerUser(UserData user) throws IOException, ResponseException {
        String path = "/user";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public AuthData login(UserData user) throws IOException, ResponseException {
        String path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public void logout(String authToken) throws IOException, ResponseException {
        String path = "/session";
        this.makeRequest("DELETE", path, null, Void.class, authToken);
    }

    public GameData createGame(GameData game, String authToken) throws IOException, ResponseException {
        String path = "/game";
        return this.makeRequest("POST", path, game, GameData.class, authToken);
    }

    public void joinGame(GamePlayerData game, String authToken) throws IOException, ResponseException {
        String path = "/game";
        this.makeRequest("PUT", path, game, Void.class, authToken);
    }

    public GameList listGames(String authToken) throws IOException, ResponseException {
        String path = "/game";
        return this.makeRequest("GET", path, null, GameList.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        HttpURLConnection connection = null;
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            if (authToken != null && !authToken.isEmpty()) {
                connection.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, connection);
            connection.connect();

            int statusCode = connection.getResponseCode();
            if (statusCode/2 != 100) {
                throw new ResponseException(statusCode, "failure: " + statusCode);
            }

            return readBody(connection, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }
}
