package chess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] piecePositions;
    Map<ChessPiece, ChessPosition> piecePositionMap;
    public ChessBoard() {
        piecePositions = new ChessPiece[8][8];
        piecePositionMap = new HashMap<>();
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        piecePositions[position.getRow()-1][position.getColumn()-1] = piece;
        piecePositionMap.put(piece, position);
    }

    public ChessPosition getPosition(ChessPiece piece) {
        return piecePositionMap.get(piece);
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor team) {
        for (Map.Entry<ChessPiece, ChessPosition> entry : piecePositionMap.entrySet()) {
            ChessPiece piece = entry.getKey();
            if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == team) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void removePiece(ChessPosition position) {
        ChessPiece piece = piecePositions[position.getRow() - 1][position.getColumn() - 1];
        piecePositions[position.getRow() - 1][position.getColumn() - 1] = null;
        piecePositionMap.remove(piece); // Remove the piece from the map
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(piecePositions, that.piecePositions);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(piecePositions);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Clear existing pieces
        piecePositionMap.clear();
        piecePositions = new ChessPiece[8][8];

        // Set up the white pieces
        for (int i = 0; i < 8; i++) {
            // Add white pawns
            addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        // Add other white pieces
        addPiece(new ChessPosition(0, 0), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(0, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(0, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(0, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(0, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(0, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(0, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(0, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        // Set up the black pieces
        for (int i = 0; i < 8; i++) {
            // Add black pawns
            addPiece(new ChessPosition(6, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        // Add other black pieces
        addPiece(new ChessPosition(7, 0), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(7, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(7, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(7, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(7, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(7, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(7, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(7, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
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
}
