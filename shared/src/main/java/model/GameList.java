package model;

import model.GameData;

public record GameList(GameData[] games) {
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (GameData game : games) { // Assuming the type of objects in 'games' is 'Game' and 'games' is a collection of 'Game'
            result.append(game.getName()).append("\n"); // Appends the name of the game followed by a newline character
        }
        return result.toString();
    }
}


