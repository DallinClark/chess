package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;
import model.GamePlayerData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Connection;

public class SqlDataAccess implements DataAccess {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void checkUsername(String username) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {

            var statement = "SELECT username FROM UserData WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username); // Set the username to check for.
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DataAccessException(403, "Error: Username already taken");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Unable to access data: %s", e.getMessage()));
        }
    }
    @Override
    public void checkGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {
        String myColor = color == ChessGame.TeamColor.WHITE ? "WHITE" : "BLACK";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM GameData WHERE gameID = ? AND (whiteUsername = ? OR blackUsername = ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                ps.setString(2, username);
                ps.setString(3, username);

                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException(404, "Error: Game not found or player not part of the game");
                    } else {
                        // Game and player found, now check if the player's color matches
                        var colorCheckStmt = "SELECT gameID FROM GameData WHERE gameID = ? AND ((whiteUsername = ? AND 'WHITE' = ?) OR (blackUsername = ? AND 'BLACK' = ?))";
                        try (var colorPs = conn.prepareStatement(colorCheckStmt)) {
                            colorPs.setInt(1, gameID);
                            colorPs.setString(2, username);
                            colorPs.setString(3, myColor);
                            colorPs.setString(4, username);
                            colorPs.setString(5, myColor);

                            try (var colorRs = colorPs.executeQuery()) {
                                if (!colorRs.next()) {
                                    // The player exists but not as the specified color
                                    throw new DataAccessException(400, "Error: Player does not match the specified color in the game");
                                }
                                // If we get here, everything checks out
                            }
                        }
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(500, String.format("Unable to access data: %s", e.getMessage()));
        }
    }
    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (user.password() == null || user.username() == null || user.email() == null) {
            throw new DataAccessException(400, "Error: bad request");
        }
        String hashedPassword = hashPassword(user.password());

        try (var conn = DatabaseManager.getConnection()) {
            // SQL INSERT statement to add a new user into the UserData table
            var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                // Set parameters for the INSERT statement
                ps.setString(1, user.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, user.email());

                // Execute the update
                int affectedRows = ps.executeUpdate();

                // Check if a row was inserted
                if (affectedRows == 0) {
                    // No rows inserted, indicating a failure in insertion
                    throw new DataAccessException(500, "Error: creating user failed, no rows affected.");
                }
            }
        } catch (SQLException e) {
            // SQLException could be thrown due to various issues like
            // connection problems, the username being already taken (if it's a unique field in your database), etc.
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var checkUserStatement = "SELECT username FROM UserData WHERE username = ?";
            try (var ps = conn.prepareStatement(checkUserStatement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        // User does not exist
                        throw new DataAccessException(400, "Error: bad request");
                    }
                }
            }
            UUID uuid = UUID.randomUUID();
            String authToken = uuid.toString();

            // Insert the new authToken into the AuthData table
            var insertTokenStatement = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(insertTokenStatement)) {
                ps.setString(1, authToken);
                ps.setString(2, username);
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    // If no rows were affected, the insert operation failed
                    throw new DataAccessException(500, "Error: Creating auth token failed, no rows affected.");
                }
            }
            return new AuthData(authToken, username);

        } catch (SQLException e) {
            // Handle potential SQLException
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    @Override
    public void checkUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // Query just for the hashed password based on the username
            var statement = "SELECT password FROM UserData WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username()); // Set the username parameter

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        // Use encoder.matches to compare the provided password with the stored hash
                        if (encoder.matches(user.password(), storedHash)) {
                            // Password matches
                            return;
                        } else {
                            // Password does not match
                            throw new DataAccessException(401, "Error: unauthorized");
                        }
                    } else {
                        // Username not found
                        throw new DataAccessException(401, "Error: unauthorized");
                    }
                }
            }
        } catch (SQLException e) {
            // Handle potential SQLExceptions from database operations
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // SQL DELETE statement to remove an auth token from the AuthData table
            var statement = "DELETE FROM AuthData WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);

                // Execute the update
                int affectedRows = ps.executeUpdate();

                // Check if a row was deleted
                if (affectedRows == 0) {
                    // If no rows were affected, the authToken did not exist
                    throw new DataAccessException(401, "Error: unauthorized");
                }
                // If we get here, the authToken was successfully deleted
            }
        } catch (SQLException e) {
            // Handle potential SQLException
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    @Override
    public void authorize(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // SQL SELECT statement to check if an auth token exists in the AuthData table
            var statement = "SELECT 1 FROM AuthData WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                // Execute the query
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException(401, "Error: unauthorized");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = -1; // Initialize gameID to an invalid value to indicate failure
        try {
            GameData newGame = new GameData();
            newGame.setGameName(gameName);
            // Assuming setGameID() is not necessary here as the DB will auto-generate it
            newGame.setGame(new ChessGame()); // Set a new ChessGame instance
            newGame.setBlackUsername(null); // Assuming null is intended for games without assigned players yet
            newGame.setWhiteUsername(null);
            // Serialize newGame to JSON
            Gson gson = new Gson();
            String gameJson = gson.toJson(newGame.getGame());

            // Insert the gameJson into the database
            try (var conn = DatabaseManager.getConnection()) {
                // Adjust the SQL INSERT statement to include all columns
                var statement = "INSERT INTO GameData (whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?)";
                try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                    // Set parameters for the PreparedStatement
                    ps.setString(1, newGame.getWhiteUsername()); // Could be null as per your logic
                    ps.setString(2, newGame.getBlackUsername()); // Could be null as well
                    ps.setString(3, newGame.getGameName()); // Game name
                    ps.setString(4, gameJson); // Serialized game

                    int affectedRows = ps.executeUpdate();

                    if (affectedRows == 0) {
                        throw new DataAccessException(400, "Creating game failed, no rows affected.");
                    }

                    try (var rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            gameID = rs.getInt(1); // Retrieve the auto-generated gameID
                        } else {
                            throw new DataAccessException(400, "Creating game failed, no ID obtained.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, "Error creating game: " + e.getMessage());
        }
        return gameID;
    }
    public ChessGame.TeamColor getUserColorFromAuthToken(String authToken, int gameID) throws DataAccessException {
        // Fetch the username associated with the authToken
        String username = userFromAuth(authToken);
        if (username == null || username.isEmpty()) {
            throw new DataAccessException(401, "Error: Unauthorized or invalid token");
        }
        // Now, check the user's color in the specified game
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername FROM GameData WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");

                        if (username.equals(whiteUsername)) {
                            return ChessGame.TeamColor.WHITE;
                        } else if (username.equals(blackUsername)) {
                            return ChessGame.TeamColor.BLACK;
                        } else {
                            throw new DataAccessException(404, "Error: User is not a participant in the specified game");
                        }
                    } else {
                        throw new DataAccessException(404, "Error: Game not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    public boolean isWatcher(String username, int gameID) throws DataAccessException {
        boolean gameExists = false;
        try (var conn = DatabaseManager.getConnection()) {
            var gameExistsQuery = "SELECT 1 FROM GameData WHERE gameID = ?";
            try (var ps = conn.prepareStatement(gameExistsQuery)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        gameExists = true; // Game exists
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Database access error checking game existence: %s", e.getMessage()));
        }
        if (!gameExists) {
            throw new DataAccessException(404, "Error: Game not found");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var watcherQuery = "SELECT 1 FROM GameWatchers WHERE gameID = ? AND username = ?";
            try (var ps = conn.prepareStatement(watcherQuery)) {
                ps.setInt(1, gameID);
                ps.setString(2, username);
                try (var rs = ps.executeQuery()) {
                    return rs.next(); // User is a watcher if we get a record
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Database access error checking watcher: %s", e.getMessage()));
        }
    }
    public void addWatcher(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO GameWatchers (gameID, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                ps.setString(2, username);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, "Database access error: " + e.getMessage());
        }
    }
    @Override
    public void joinGame(GamePlayerData game, String authToken) throws DataAccessException {
        // Fetch the username associated with the authToken
        String username = this.userFromAuth(authToken);
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            // Check the current state of the game
            var statement = "SELECT whiteUsername, blackUsername FROM GameData WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, game.gameID());
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException(400, "Error: bad request");
                    }
                    if (game.playerColor() == null || game.playerColor().isEmpty()) {
                        addWatcher(game.gameID(), authToken);
                    }
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");

                    if ("WHITE".equals(game.playerColor()) && whiteUsername == null) {
                        updatePlayerColor(conn, game.gameID(), "whiteUsername", username);
                    } else if ("BLACK".equals(game.playerColor()) && blackUsername == null) {
                        updatePlayerColor(conn, game.gameID(), "blackUsername", username);
                    } else {
                        // If the requested color is already taken or an invalid color was provided
                        conn.rollback(); // Roll back to maintain consistent state
                    }
                    conn.commit(); // Commit the transaction
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    private void updatePlayerColor(Connection conn, int gameID, String colorColumn, String username) throws SQLException {
        var updateStatement = "UPDATE GameData SET " + colorColumn + " = ? WHERE gameID = ?";
        try (var ps = conn.prepareStatement(updateStatement)) {
            ps.setString(1, username);
            ps.setInt(2, gameID);
            ps.executeUpdate();
        }
    }
    @Override
    public String userFromAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // SQL SELECT statement to retrieve the username associated with the authToken
            var statement = "SELECT username FROM AuthData WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    } else {
                        throw new DataAccessException(401, "Error: unauthorized");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    @Override
    public GameData[] listGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            // SQL SELECT statement to retrieve all games
            var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, json FROM GameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // For each record, create a new GameData object and populate it
                        GameData game = new GameData();
                        game.setGameID(rs.getInt("gameID"));
                        game.setGameName(rs.getString("gameName"));
                        game.setWhiteUsername(rs.getString("whiteUsername"));
                        game.setBlackUsername(rs.getString("blackUsername"));

                        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                        String json = rs.getString("json");
                        ChessGame chessGame = gson.fromJson(json, ChessGame.class);
                        game.setGame(chessGame);
                        gameList.add(game);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle potential SQLExceptions
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
        return gameList.toArray(new GameData[0]);
    }
    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // Clear the Users table
            var clearUsers = "DELETE FROM UserData";
            conn.prepareStatement(clearUsers).executeUpdate();
            // Clear the Games table
            var clearGames = "DELETE FROM GameData";
            conn.prepareStatement(clearGames).executeUpdate();
            // Clear the AuthData table
            var clearTokens = "DELETE FROM AuthData";
            conn.prepareStatement(clearTokens).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }
    private String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ;
            """,
            """
            CREATE TABLE IF NOT EXISTS  GameData (
                `gameID` int NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(256),
                `blackUsername` varchar(256),
                `gameName` varchar(256),
                `json` TEXT DEFAULT NULL,
                PRIMARY KEY (`gameID`)
            ) ;
            """,
            """
            CREATE TABLE IF NOT EXISTS   UserData (
                `username` varchar(256),
                `password` varchar(256),
                `email` varchar(256),
                PRIMARY KEY (`username`)
            ) ;
            """,
            """
            CREATE TABLE IF NOT EXISTS GameWatchers (
                `gameID` int,
                `username` varchar(255),
                PRIMARY KEY (`username`)
            );
            """
    };
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            // Recreate tables if they do not exist
            for (String stmt : createStatements) {
                try (var preparedStatement = conn.prepareStatement(stmt)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
