package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import  chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor.*;

public class PrintBoard {

    public static void printGameBoard(ChessBoard board) {
        StringBuilder builder = new StringBuilder();

        // Top border
        builder.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY)
                .append("  a b c d e f g h")
                .append(EscapeSequences.RESET_TEXT_COLOR)
                .append("\n");

        for (int row = 8; row >= 1; row--) {
            builder.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY)
                    .append(row)
                    .append(EscapeSequences.RESET_TEXT_COLOR);

            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(col, row);
                ChessPiece piece = board.getPiece(position);

                // Alternate cell colors for better visibility
                if ((row + col) % 2 == 0) {
                    builder.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    builder.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                String pieceSymbol = getPieceSymbol(piece);
                builder.append(pieceSymbol)
                        .append(EscapeSequences.RESET_BG_COLOR); // Reset background color after each cell
            }
            builder.append("\n");
        }

        System.out.println(builder.toString());
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

