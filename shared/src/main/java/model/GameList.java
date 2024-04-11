package model;

import chess.ChessGame;
import model.GameData;

public record GameList(GameData[] games) {


    public String toString() {
        int i = 1;
        StringBuilder result = new StringBuilder();
        for (GameData game : games) { // Assuming the type of objects in 'games' is 'Game' and 'games' is a collection of 'Game'
            result.append(i).append(": ");
            result.append(game.getName()).append("   ");
            result.append("WHITE: ").append(game.getWhiteUsername()).append(" BLACK: ").append(game.getBlackUsername()).append("\n"); // Appends the name of the game followed by a newline character
            i += 1;
        }
        return result.toString();
    }
    public int getIdFromIndex(int index) {
        return games[index - 1].getGameID();
    }
    public GameData[] getGames() {
        return games;
    }
}


