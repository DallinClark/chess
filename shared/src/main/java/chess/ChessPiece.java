package chess;

import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType myType;
    ChessGame.TeamColor myColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        myType = type;
        myColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return myColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return myType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var ret = new HashSet<ChessMove>();
        if (myType == PieceType.BISHOP) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            int i = row + 1;
            int j = col + 1;
            ChessPosition currPos = null;

            while (i < 9 && j < 9) {
                currPos = new ChessPosition(i, j);
                ChessMove move = new ChessMove(myPosition, currPos, null);
                if (board.getPiece(currPos) != null) {
                    if (board.getPiece(currPos).getTeamColor() != myColor) {
                        ret.add(move); // Capture move
                    }
                    break;
                }
                else { ret.add(move); }
                ++i; ++j;
            }
            i = row - 1; j = col - 1;
            while (i > 0 && j > 0) {
                currPos = new ChessPosition(i, j);
                ChessMove move = new ChessMove(myPosition, currPos, null);
                if (board.getPiece(currPos) != null) {
                    if (board.getPiece(currPos).getTeamColor() != myColor) {
                        ret.add(move);
                    }
                    break;
                }
                else { ret.add(move); }
                --i; --j;
            }
            i = row + 1; j = col - 1;
            while (i < 9 && j > 0) {
                currPos = new ChessPosition(i, j);
                ChessMove move = new ChessMove(myPosition, currPos, null);
                if (board.getPiece(currPos) != null) {
                    if (board.getPiece(currPos).getTeamColor() != myColor) {
                        ret.add(move);
                    }
                    break;
                }
                else { ret.add(move); }
                ++i; --j;
            }
            i = row - 1; j = col + 1;
            while (i > 0 && j < 9) {
                currPos = new ChessPosition(i, j);
                ChessMove move = new ChessMove(myPosition, currPos, null);
                if (board.getPiece(currPos) != null) {
                    if (board.getPiece(currPos).getTeamColor() != myColor) {
                        ret.add(move);
                    }
                    break;
                }
                else { ret.add(move); }
                --i; ++j;
            }
        }
        else if (myType == PieceType.KING) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            ChessPosition currPos = null;

            for (int i = -1; i < 2; ++i){
                if (row + i < 9 && row + i > 0) {
                    for (int j = -1; j < 2; ++j) {
                        if (col + j < 9 && col + j > 0) {
                            currPos = new ChessPosition(row + i,col + j);
                            ChessMove move = new ChessMove(myPosition, currPos, null);

                            if (board.getPiece(currPos) != null) {
                                if (board.getPiece(currPos).getTeamColor() != myColor) {
                                    ret.add(move);
                                }
                                break;
                            }
                            else { ret.add(move); }
                        }
                    }
                }
            }


        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return myType == that.myType && myColor == that.myColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myType, myColor);
    }
}
