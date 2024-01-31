package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;
    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece != null) {
            return piece.pieceMoves(board, startPosition);
        }
        else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        for (ChessMove validMove : board.getPiece(move.getStartPosition()).pieceMoves(board, move.getStartPosition())) {
            if (move.equals(validMove)) {
                board.movePiece(move);
                return;
            }
        }
        throw new InvalidMoveException("Invalid Move");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor oppTeam;
        ChessPosition kingPos = board.getKingPosition(teamColor);
        if (teamColor == TeamColor.WHITE) {
            oppTeam = TeamColor.BLACK;
        }
        else {
            oppTeam = TeamColor.WHITE;
        }
        for (ChessPiece piece : board.getPieces(oppTeam)) {
            ChessPosition piecePos = board.getPosition(piece);
            for (ChessMove move : piece.pieceMoves(board, piecePos)) {
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPiece kingPiece = board.getKingPiece(teamColor);
        ChessPosition kingPos = board.getPosition(kingPiece);
        try {
            if (isInCheck(teamColor)) {
                for (ChessMove move : kingPiece.pieceMoves(board, kingPos)) {
                    ChessPiece pieceAtEndPos = board.getPiece(move.getEndPosition());

                    // Try the move
                    makeMove(move);
                    if (!isInCheck(teamColor)) {
                        // If the king is not in check after the move, it's not checkmate
                        // Revert the move and return false
                        ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                        makeMove(reverseMove);
                        if (pieceAtEndPos != null) {
                            board.addPiece(move.getEndPosition(), pieceAtEndPos);
                        }
                        return false;
                    }

                    // Revert the move
                    ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                    makeMove(reverseMove);
                    if (pieceAtEndPos != null) {
                        board.addPiece(move.getEndPosition(), pieceAtEndPos);
                    }
                }
                return true; // No moves take the king out of check, so it's checkmate
            } else {
                return false; // Not in check, so not checkmate
            }
        } catch (InvalidMoveException e) {
            // Handle the exception here
            System.out.println(e.getMessage());
            // Needs Additional logic to handle invalid move, like prompting user for a new move
        }
        return false; // placeholder
    }



    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            try {
                for (ChessPiece piece : board.getPieces(teamColor)) {
                    for (ChessMove move : piece.pieceMoves(board, board.getPosition(piece))) {
                        ChessPiece pieceAtEndPos = board.getPiece(move.getEndPosition());
                        makeMove(move);
                        if (!isInCheck(teamColor)) {
                            ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                            makeMove(reverseMove);
                            if (pieceAtEndPos != null) {
                                board.addPiece(move.getEndPosition(), pieceAtEndPos);
                            }
                            return false;
                        }
                        ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                        makeMove(reverseMove);
                        if (pieceAtEndPos != null) {
                            board.addPiece(move.getEndPosition(), pieceAtEndPos);
                        }
                    }
                }
            } catch (InvalidMoveException e) {
                // Handle the exception here
                System.out.println(e.getMessage());
                // Needs Additional logic to handle invalid move, like prompting user for a new move
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param newBoard the new board to use
     */
    public void setBoard(ChessBoard newBoard) {
        board = newBoard;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
