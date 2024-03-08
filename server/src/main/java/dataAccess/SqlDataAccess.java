package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Connection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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
                    // If we reach here, the user exists, so we proceed to create an auth token.
                }
            }

            // Generate a new UUID for the authToken
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

            // Return the new AuthData object
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
                    if (rs.next()) {
                        // If the query finds a record, it means the authToken is valid
                        return;
                    } else {
                        // If no record is found, throw an exception indicating unauthorized access
                        throw new DataAccessException(401, "Error: unauthorized");
                    }
                }
            }
        } catch (SQLException e) {
            // Handle potential SQLException
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = -1; // Initialize gameID to an invalid value to indicate failure
        try {
            // Initialize the new GameData object
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
                        return; //TODO add them as watcher
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
                        throw new DataAccessException(403, "Error: forbidden");
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
                        // If a record is found, return the username
                        return rs.getString("username");
                    } else {
                        // If no record is found, throw an exception indicating unauthorized access
                        throw new DataAccessException(401, "Error: unauthorized");
                    }
                }
            }
        } catch (SQLException e) {
            // Handle potential SQLException
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

                        String json = rs.getString("json");
                        ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);
                        game.setGame(chessGame);

                        gameList.add(game);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle potential SQLExceptions
            throw new DataAccessException(500, String.format("Database access error: %s", e.getMessage()));
        }

        // Convert the list to an array and return it
        return gameList.toArray(new GameData[0]);
    }


    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // Disable foreign key checks if your database enforces referential integrity and you encounter constraints errors
            // This is MySQL-specific; adjust accordingly for other databases
            // var disableFkChecks = "SET FOREIGN_KEY_CHECKS = 0";
            // conn.prepareStatement(disableFkChecks).execute();

            // Clear the Users table
            var clearUsers = "DELETE FROM UserData";
            conn.prepareStatement(clearUsers).executeUpdate();

            // Clear the Games table
            var clearGames = "DELETE FROM GameData";
            conn.prepareStatement(clearGames).executeUpdate();

            // Clear the AuthData table
            var clearTokens = "DELETE FROM AuthData";
            conn.prepareStatement(clearTokens).executeUpdate();

            // Re-enable foreign key checks if they were disabled
            // var enableFkChecks = "SET FOREIGN_KEY_CHECKS = 1";
            // conn.prepareStatement(enableFkChecks).execute();

            // Reset auto-increment values if necessary (highly database-specific, shown here for MySQL)
            // var resetUsersAutoIncrement = "ALTER TABLE Users AUTO_INCREMENT = 1";
            // conn.prepareStatement(resetUsersAutoIncrement).executeUpdate();
            // var resetGamesAutoIncrement = "ALTER TABLE Games AUTO_INCREMENT = 1";
            // conn.prepareStatement(resetGamesAutoIncrement).executeUpdate();
            // var resetAuthDataAutoIncrement = "ALTER TABLE AuthData AUTO_INCREMENT = 1";
            // conn.prepareStatement(resetAuthDataAutoIncrement).executeUpdate();

        } catch (SQLException e) {
            // Handle potential SQLExceptions
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
