package chess;

import java.util.ArrayList;
import java.util.Collection;

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
        Collection<ChessMove> ret = new ArrayList<ChessMove>();
        if (myType == PieceType.BISHOP) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            int i = row + 1;
            int j = col + 1;
            ChessPosition currPos = null;

            while (i < 9 && j < 9) {
                currPos = new ChessPosition(i, j);
                if (board.getPiece(currPos) == null) {
                    ChessMove move = new ChessMove(myPosition, currPos, null);
                    ret.add(move);
                }
                else { break; }
                ++i; ++j;
            }
            i = row - 1; j = col - 1;
            while (i > 0 && j > 0) {
                currPos = new ChessPosition(i, j);
                if (board.getPiece(currPos) == null) {
                    ChessMove move = new ChessMove(myPosition, currPos, null);
                    ret.add(move);
                }
                else { break; }
                --i; --j;
            }
            i = row + 1; j = col - 1;
            while (i < 9 && j > 0) {
                currPos = new ChessPosition(i, j);
                if (board.getPiece(currPos) == null) {
                    ChessMove move = new ChessMove(myPosition, currPos, null);
                    ret.add(move);
                }
                else { break; }
                ++i; --j;
            }
            i = row - 1; j = col + 1;
            while (i > 0 && j < 9) {
                currPos = new ChessPosition(i, j);
                if (board.getPiece(currPos) == null) {
                    ChessMove move = new ChessMove(myPosition, currPos, null);
                    ret.add(move);
                }
                else { break; }
                --i; ++j;
            }
        }
        return ret;
    }
}
