package ui;

import chess.*;

import  chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor.*;

import java.util.Collection;

public class PrintBoard {

    public static void printGameBoard(ChessBoard board, String color) {
        StringBuilder builder = new StringBuilder();

        // Determine if the current player is black to decide the order of printing
        boolean isBlack = "BLACK".equalsIgnoreCase(color);

        // Column labels
        printColumnLabels(builder, isBlack);

        // Adjust row printing order based on the player's color
        for (int row = isBlack ? 8 : 1; isBlack ? row >= 1 : row <= 8; row += isBlack ? -1 : 1) {
            builder.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY)
                    .append(row)
                    .append(EscapeSequences.RESET_TEXT_COLOR);

            // Adjust column printing order based on the player's color
            for (int col = isBlack ? 1 : 8; isBlack ? col <= 8 : col >= 1; col += isBlack ? 1 : -1) {
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
        if (isBlack) {
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
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_KING : EscapeSequences.WHITE_KING;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_QUEEN : EscapeSequences.WHITE_QUEEN;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_BISHOP : EscapeSequences.WHITE_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_KNIGHT : EscapeSequences.WHITE_KNIGHT;
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_ROOK : EscapeSequences.WHITE_ROOK;
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_PAWN : EscapeSequences.WHITE_PAWN;
            default:
                return EscapeSequences.EMPTY;
        }
    }

    public static void printGameBoardHighlighted(ChessBoard board, String color, Collection<ChessMove> legalMoves) {
        StringBuilder builder = new StringBuilder();

        // Determine if the current player is black to decide the order of printing
        boolean isBlack = "BLACK".equalsIgnoreCase(color);

        // Column labels
        printColumnLabels(builder, isBlack);

        // Adjust row printing order based on the player's color
        for (int row = isBlack ? 8 : 1; isBlack ? row >= 1 : row <= 8; row += isBlack ? -1 : 1) {
            builder.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY)
                    .append(row)
                    .append(EscapeSequences.RESET_TEXT_COLOR);

            // Adjust column printing order based on the player's color
            for (int col = isBlack ? 1 : 8; isBlack ? col <= 8 : col >= 1; col += isBlack ? 1 : -1) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Check if the current cell is a legal move end position
                boolean isLegalMove = legalMoves.stream()
                        .anyMatch(move -> move.getEndPosition().equals(position));

                // Highlight cell if it's a legal move position
                if (isLegalMove) {
                    builder.append(EscapeSequences.SET_BG_COLOR_YELLOW);
                } else if ((row + col) % 2 == 0) {
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

    public static void printGameBoardHighlghted(ChessBoard board, String color, Collection<ChessMove> legalMoves) {
        StringBuilder builder = new StringBuilder();

        // Determine if the current player is black to decide the order of printing
        boolean isBlack = "BLACK".equalsIgnoreCase(color);

        // Column labels
        printColumnLabels(builder, isBlack);

        // Adjust row printing order based on the player's color
        for (int row = isBlack ? 8 : 1; isBlack ? row >= 1 : row <= 8; row += isBlack ? -1 : 1) {
            builder.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY)
                    .append(row)
                    .append(EscapeSequences.RESET_TEXT_COLOR);

            // Adjust column printing order based on the player's color
            for (int col = isBlack ? 1 : 8; isBlack ? col <= 8 : col >= 1; col += isBlack ? 1 : -1) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Check if the current cell is a legal move end position
                boolean isLegalMove = legalMoves.stream()
                        .anyMatch(move -> move.getEndPosition().equals(position));

                // Highlight cell if it's a legal move position
                if (isLegalMove) {
                    builder.append(EscapeSequences.SET_BG_COLOR_YELLOW);
                } else if ((row + col) % 2 == 0) {
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
}

