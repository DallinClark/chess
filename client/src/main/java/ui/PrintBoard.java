package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import  chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor.*;

public class PrintBoard {

    public static void printGameBoard(ChessBoard board, String color) {
        StringBuilder builder = new StringBuilder();

        // Determine if the current player is black to decide the order of printing
        boolean isBlack = "BLACK".equalsIgnoreCase(color);

        // Column labels
        printColumnLabels(builder, isBlack);

        builder.append("\n");

        // Adjust row printing order based on the player's color
        for (int row = isBlack ? 1 : 8; isBlack ? row <= 8 : row >= 1; row += isBlack ? 1 : -1) {
            builder.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY)
                    .append(9 - row)
                    .append(EscapeSequences.RESET_TEXT_COLOR);

            // Adjust column printing order based on the player's color
            for (int col = isBlack ? 8 : 1; isBlack ? col >= 1 : col <= 8; col += isBlack ? -1 : 1) {
                // Assuming ChessPosition constructor takes (row, column) in that order
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Alternate cell colors for better visibility
                if ((row + col) % 2 == 0) {
                    builder.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    builder.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                String pieceSymbol = getPieceSymbol(piece);
                builder.append(" ").append(pieceSymbol)
                        .append(EscapeSequences.RESET_BG_COLOR); // Reset background color after each cell
            }
            builder.append("\n");
        }

        System.out.println(builder.toString());
    }

    private static void printColumnLabels(StringBuilder builder, boolean isBlack) {
        builder.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        if (!isBlack) {
            builder.append("   a    b    c   d    e    f    g    h");
        } else {
            builder.append("   h    g    f   e    d    c    b    a"); // Reverse order for black
        }
        builder.append(EscapeSequences.RESET_TEXT_COLOR)
                .append("\n");
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY; // Placeholder for empty cell
        }

        switch (piece.getPieceType()) {
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default:
                return EscapeSequences.EMPTY;
        }
    }
}

