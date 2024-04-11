package chess;

import java.util.*;

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
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
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
        TeamColor team = piece.getTeamColor();
        List<ChessMove> validMoves = new ArrayList<>();

        if (team != teamTurn && teamTurn != null) {
            return validMoves;
        }

        for (ChessMove move : piece.pieceMoves(board,startPosition)) {
            ChessPiece pieceAtEndPos = board.getPiece(move.getEndPosition());

            board.movePiece(move);
            if (!isInCheck(team)) {
                validMoves.add(move);
            }

            ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
            board.movePiece(reverseMove);
            if (pieceAtEndPos != null) {
                board.addPiece(move.getEndPosition(), pieceAtEndPos);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        for (ChessMove currMove : validMoves(move.getStartPosition())) {
            if (move.equals(currMove)) {
                board.movePiece(move);
                if (teamTurn == TeamColor.WHITE) {
                    teamTurn = TeamColor.BLACK;
                }
                else {
                    teamTurn = TeamColor.WHITE;
                }
                if (isInCheckmate(teamTurn)) {
                    String winner;
                    if (teamTurn == TeamColor.WHITE) {
                        winner = "Black";
                    }
                    else {
                        winner = "White";
                    }
                    throw new InvalidMoveException("Checkmate! " + winner + " wins!");
                }
                if (isInStalemate(teamTurn)) {
                    throw new InvalidMoveException("Stalemate, game over :(");
                }
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
        TeamColor oppTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition kingPos = board.getKingPosition(teamColor);

        if (kingPos == null) {
            return false; // This should not happen in a valid game state
        }

        // Iterate over all pieces of the opposing team to see if any can move to the king's position
        for (ChessPiece piece : board.getPieces(oppTeam)) {
            ChessPosition piecePos = board.getPosition(piece);

            // For each piece, generate all valid moves considering the current board state
            for (ChessMove move : piece.pieceMoves(board, piecePos)) {
                // Check if any move ends at the king's position
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false; // If no opposing piece can legally move to the king's position, not in check
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

        if (isInCheck(teamColor)) {
            for (ChessMove move : kingPiece.pieceMoves(board, kingPos)) {
                ChessPiece pieceAtEndPos = board.getPiece(move.getEndPosition());

                // Try the move
                board.movePiece(move);
                if (!isInCheck(teamColor)) {
                    // If the king is not in check after the move, it's not checkmate
                    // Revert the move and return false
                    ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                    board.movePiece(reverseMove);
                    if (pieceAtEndPos != null) {
                        board.addPiece(move.getEndPosition(), pieceAtEndPos);
                    }
                    return false;
                }

                // Revert the move
                ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                board.movePiece(reverseMove);
                if (pieceAtEndPos != null) {
                    board.addPiece(move.getEndPosition(), pieceAtEndPos);
                }
            }
            return true; // No moves take the king out of check, so it's checkmate
        } else {
            return false; // Not in check, so not checkmate
        }
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
            for (ChessPiece piece : board.getPieces(teamColor)) {
                for (ChessMove move : piece.pieceMoves(board, board.getPosition(piece))) {
                    ChessPiece pieceAtEndPos = board.getPiece(move.getEndPosition());
                    board.movePiece(move);
                    if (!isInCheck(teamColor)) {
                        ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                        board.movePiece(reverseMove);
                        if (pieceAtEndPos != null) {
                            board.addPiece(move.getEndPosition(), pieceAtEndPos);
                        }
                        return false;
                    }
                    ChessMove reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
                    board.movePiece(reverseMove);
                    if (pieceAtEndPos != null) {
                        board.addPiece(move.getEndPosition(), pieceAtEndPos);
                    }
                }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getBoard());
    }
}
