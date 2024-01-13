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
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPosition currPos = null;

        if (myType == PieceType.BISHOP) {
            int i = row + 1;
            int j = col + 1;

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
                            }
                            else { ret.add(move); }
                        }
                    }
                }
            }
        }
        else if (myType == PieceType.KNIGHT) {
            int [] rowPos = new int[]{2,2,1,1,-1,-1,-2,-2};
            int [] colPos = new int[]{1,-1,2,-2,2,-2,1,-1};
            for (int i = 0; i < rowPos.length; ++i) {
                if (row + rowPos[i] > 0 && row + rowPos[i] < 9 && col + colPos[i] > 0 && col + colPos[i] < 9) {
                    currPos = new ChessPosition(row + rowPos[i],col + colPos[i]);
                    ChessMove move = new ChessMove(myPosition, currPos, null);

                    if (board.getPiece(currPos) != null) {
                        if (board.getPiece(currPos).getTeamColor() != myColor) {
                            ret.add(move);
                        }
                    }
                    else { ret.add(move); }
                }
            }

        }
        else if (myType == PieceType.PAWN) {
            int direction = 1;
            ChessMove move;
            if (myColor == ChessGame.TeamColor.BLACK) {
                direction = -1;
            }
            // Checking if it can go one or two ahead
            currPos = new ChessPosition(row + direction, col);
            if (board.getPiece(currPos) == null) {
                // Checking if this move is a promotion
                if ((row == 2 && myColor == ChessGame.TeamColor.BLACK) || (row == 7 && myColor == ChessGame.TeamColor.WHITE)) {
                    move = new ChessMove(myPosition, currPos, PieceType.QUEEN);
                    ret.add(move);
                    move = new ChessMove(myPosition, currPos, PieceType.BISHOP);
                    ret.add(move);
                    move = new ChessMove(myPosition, currPos, PieceType.ROOK);
                    ret.add(move);
                    move = new ChessMove(myPosition, currPos, PieceType.KNIGHT);
                    ret.add(move);
                }
                else {
                    move = new ChessMove(myPosition, currPos, null);
                    ret.add(move);
                    //Checking if it can go two ahead
                    if ((row == 2 && myColor == ChessGame.TeamColor.WHITE) || (row == 7 && myColor == ChessGame.TeamColor.BLACK)) {
                        currPos = new ChessPosition(row + (direction * 2), col);
                        if (board.getPiece(currPos) == null) {
                            move = new ChessMove(myPosition, currPos, null);
                            ret.add(move);
                        }
                    }

                }
            }
            // Checking if it can take
            if (col - 1 > 0) {
                currPos = new ChessPosition(row + direction, col - 1);
                if (board.getPiece(currPos) != null && board.getPiece(currPos).getTeamColor() != myColor ) {
                    if (row + direction == 1 || row + direction == 8) {
                        move = new ChessMove(myPosition, currPos, PieceType.QUEEN);
                        ret.add(move);
                        move = new ChessMove(myPosition, currPos, PieceType.BISHOP);
                        ret.add(move);
                        move = new ChessMove(myPosition, currPos, PieceType.ROOK);
                        ret.add(move);
                        move = new ChessMove(myPosition, currPos, PieceType.KNIGHT);
                        ret.add(move);
                    }
                    else {
                        move = new ChessMove(myPosition, currPos, null);
                        ret.add(move);
                    }
                }
            }
            if (col + 1 < 9) {
                currPos = new ChessPosition(row + direction, col + 1);
                if (board.getPiece(currPos) != null && board.getPiece(currPos).getTeamColor() != myColor ) {
                    if (row + direction == 1 || row + direction == 8) {
                        move = new ChessMove(myPosition, currPos, PieceType.QUEEN);
                        ret.add(move);
                        move = new ChessMove(myPosition, currPos, PieceType.BISHOP);
                        ret.add(move);
                        move = new ChessMove(myPosition, currPos, PieceType.ROOK);
                        ret.add(move);
                        move = new ChessMove(myPosition, currPos, PieceType.KNIGHT);
                        ret.add(move);
                    }
                    else {
                        move = new ChessMove(myPosition, currPos, null);
                        ret.add(move);
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
