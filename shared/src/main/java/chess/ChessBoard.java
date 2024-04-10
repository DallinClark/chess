package chess;
import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] piecePositions;


    public ChessBoard() {
        piecePositions = new ChessPiece[8][8];
        //piecePositionMap = new HashMap<>();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        piecePositions[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void movePiece(ChessMove move) {
        if (move.getPromotionPiece() != null) {
            getPiece(move.getStartPosition()).changeType(move.getPromotionPiece());
        }
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = getPiece(startPos);
        piecePositions[startPos.getRow() - 1][startPos.getColumn() - 1] = null;
        piecePositions[endPos.getRow() - 1][endPos.getColumn() - 1] = piece;
    }

    public ChessPosition getPosition(ChessPiece piece) {
        for (int row = 0; row < piecePositions.length; row++) {
            for (int column = 0; column < piecePositions[row].length; column++) {
                ChessPiece currentPiece = piecePositions[row][column];
                if (currentPiece != null && currentPiece.equals(piece)) {
                    return new ChessPosition(row + 1, column + 1);
                }
            }
        }
        return null; // Piece not found
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor team) {
        for (int row = 0; row < piecePositions.length; row++) {
            for (int column = 0; column < piecePositions[row].length; column++) {
                ChessPiece currentPiece = piecePositions[row][column];
                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == team) {
                    return new ChessPosition(row + 1, column + 1);
                }
            }
        }
        return null; // King not found or something went wrong
    }

    public ChessPiece getKingPiece(ChessGame.TeamColor team) {
        for (int row = 0; row < piecePositions.length; row++) {
            for (int column = 0; column < piecePositions[row].length; column++) {
                ChessPiece currentPiece = piecePositions[row][column];
                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == team) {
                    return currentPiece;
                }
            }
        }
        return null; // King not found
    }


    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        if (row < 1 || row > 9 || column < 1 || column > 9) {
            return null;
        }
        return piecePositions[position.getRow()-1][position.getColumn()-1];
    }


    public List<ChessPiece> getPieces(ChessGame.TeamColor team) {
        List<ChessPiece> teamPieces = new ArrayList<>();
        for (ChessPiece[] row : piecePositions) {
            for (ChessPiece piece : row) {
                if (piece != null && piece.getTeamColor() == team) {
                    teamPieces.add(piece);
                }
            }
        }
        return teamPieces;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Clear existing pieces
        //piecePositionMap.clear();
        piecePositions = new ChessPiece[8][8];

        // Set up the white pieces
        for (int i = 1; i <= 8; i++) {
            // Add white pawns
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        // Add other white pieces
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        // Set up the black pieces
        for (int i = 1; i <= 8; i++) {
            // Add black pawns
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        // Add other black pieces
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }



    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            out.append("|");
            for (int j = 0; j < 8; ++j) {
                if (piecePositions[i][j] == null) {
                    out.append(" ");
                }
                else {
                    out.append(piecePositions[i][j].toString());
                }
                out.append("|");
            }
            out.append("\n");
        }
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;

        for (int i = 0; i < piecePositions.length; i++) {
            for (int j = 0; j < piecePositions[i].length; j++) {
                ChessPiece thisPiece = piecePositions[i][j];
                ChessPiece thatPiece = that.piecePositions[i][j];
                if (thisPiece == null && thatPiece == null) {
                    continue; // Both positions are empty, so they are equal
                }
                if (thisPiece == null || thatPiece == null) {
                    return false; // One is null and the other isn't, so they are not equal
                }
                if (thisPiece.getPieceType() != thatPiece.getPieceType() ||
                        thisPiece.getTeamColor() != thatPiece.getTeamColor()) {
                    return false; // Different type or color
                }
            }
        }
        return true; // All pieces match in type and color
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (ChessPiece[] row : piecePositions) {
            for (ChessPiece piece : row) {
                result = 31 * result + (piece == null ? 0 : piece.hashCode());
            }
        }
        return result;
    }

}
