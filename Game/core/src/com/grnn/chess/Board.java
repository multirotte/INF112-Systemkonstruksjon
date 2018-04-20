package com.grnn.chess;

import com.grnn.chess.exceptions.IllegalMoveException;
import com.grnn.chess.objects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a board
 */

public class Board {

    // Variables
    private int size = 8;
    private ArrayList<ArrayList<AbstractChessPiece>> grid = new ArrayList<ArrayList<AbstractChessPiece>>(size);
    private ArrayList<AbstractChessPiece> removedPieces;
    private ArrayList<Move> moveHistory;
    private int halfmoveNumber;

    public ArrayList<Position> positions;

    public Board() {
        moveHistory = new ArrayList<Move>();
        removedPieces = new ArrayList<AbstractChessPiece>();
        positions = new ArrayList<Position>();

        for (int i = 0; i < size; i++) {
            grid.add(new ArrayList<AbstractChessPiece>(size));
            for (int j = 0; j < size; j++) {
                grid.get(i).add(null);
                positions.add(new Position(j, i));
            }
        }
    }


    public void removePiece(AbstractChessPiece piece) {
        removedPieces.add(piece);
    }

    public ArrayList<AbstractChessPiece> getRemovedPieces() {
        return removedPieces;
    }

    public void movePiece(Position startPos, Position endPos) {
        AbstractChessPiece piece = getPieceAt(startPos);
        AbstractChessPiece capturePiece = getPieceAt(endPos);

        //if (isValidMove(startPos, endPos)) {
        setPiece(piece, endPos);
        setPiece(null, startPos);
        piece.move();
        moveHistory.add(new Move(endPos, startPos, piece));
        enPassant();

        if(capturePiece == null || piece instanceof Pawn) {
            halfmoveNumber = 0;
        } else {
            halfmoveNumber++;
        }
    }

    public void castle(Position startPos, Position endPos){
        AbstractChessPiece piece = getPieceAt(startPos);

        setPiece(piece, endPos);
        setPiece(null, startPos);
        piece.move();
        moveHistory.add(new Move(endPos, startPos, piece));
        setPiece(piece, endPos);
        setPiece(null, startPos);
        piece.move();
        moveHistory.add(new Move(endPos, startPos, piece));
        enPassant();
    }

    public Position getKingPos(boolean kingIsWhite){
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                if (getPieceAt(new Position(i, j)) instanceof King && getPieceAt(new Position(i, j)).isWhite()==kingIsWhite){
                    return new Position(i, j);
                }
            }
        }
        return null;
    }



    private boolean isValidMove(Position startPos, Position endPos) {
        AbstractChessPiece piece = getPieceAt(startPos);
        boolean containsCastlingMove;
        if(piece instanceof King) {
            ArrayList<Position> castlingMoves = ((King) piece).getCastlingMoves(this, startPos);
            containsCastlingMove = castlingMoves.contains(endPos);
        } else {
            containsCastlingMove = false;
        }

        return piece.getValidMoves(this).contains(endPos)
                || piece.getCaptureMoves(this).contains(endPos)
                || containsCastlingMove;
    }

    // TODO: AI is not always black

    /**
     * Gets possible moves the AI can do.
     * @return possible moves the AI can do.
     * @param isWhite
     */
    public ArrayList<Move> getPossibleAIMoves(boolean isWhite) { //Aka get black's moves

        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        for(Position position : positions){
            AbstractChessPiece piece = this.getPieceAt(position);
            if(piece!=null && isWhite == piece.isWhite()) {
                ArrayList<Position> posList = piece.getValidMoves(this);
                posList.addAll(piece.getCaptureMoves(this));
                if (!posList.isEmpty()){
                    for (Position toMove : posList) {
                        Move newMove = new Move(toMove, position, piece);
                        possibleMoves.add(newMove);
                    }
                }
            }
        }
        return possibleMoves;
    }


    public Board updateBoard() {
        return null;
    }

    public int size() {
        return grid.size();
    }

    /**
     * Initializes the board with pieces in standard starting positions
     */
    public void initializeBoard() {

        for (int i = 0; i < size(); i++) {
            setPiece(new Pawn(true), i, 1);
            setPiece(new Pawn(false), i, 6);
        }

        setPiece(new Rook(true), 0, 0);
        setPiece(new Rook(true), 7, 0);

        setPiece(new Rook(false), 0, 7);
        setPiece(new Rook(false), 7, 7);

        setPiece(new Knight(true), 1, 0);
        setPiece(new Knight(true), 6, 0);

        setPiece(new Knight(false), 1, 7);
        setPiece(new Knight(false), 6, 7);

        setPiece(new Bishop(true), 2, 0);
        setPiece(new Bishop(true), 5, 0);
        setPiece(new Bishop(false), 2, 7);
        setPiece(new Bishop(false), 5, 7);

        setPiece(new Queen(true), 3, 0);
        setPiece(new Queen(false), 3, 7);

        setPiece(new King(true), 4, 0);
        setPiece(new King(false), 4, 7);
    }

    public void setPiece(AbstractChessPiece piece, Position pos) {
        setPiece(piece, pos.getX(), pos.getY());
    }

    public void setPiece(AbstractChessPiece piece, int x, int y) {
        if (pawnCanPromote(piece, y)) {
            piece = new Queen(piece.isWhite());
        }
        grid.get(y).set(x, piece);
    }

    private boolean pawnCanPromote(AbstractChessPiece piece, int y) {
        if (piece instanceof Pawn) {
            if ((piece.isWhite() && y == size() - 1)
                    || (!piece.isWhite() && y == 0)) {
                return true;
            }
        }
        return false;
    }

    public AbstractChessPiece getPieceAt(Position p) {
        if (posIsWithinBoard(p)) {
            return grid.get(p.getY()).get(p.getX());
        }
        return null;
    }

    public Position getPosition(AbstractChessPiece piece) {
        for (int y = 0; y < size(); y++) {
            for (int x = 0; x < size(); x++) {
                Position p = new Position(x, y);
                if (getPieceAt(p) != null && getPieceAt(p).equals(piece))
                    return p;
            }
        }
        return null;
    }

    public boolean posIsWithinBoard(Position pos) {
        return (pos.getX() >= 0 && pos.getX() < size && pos.getY() >= 0 && pos.getY() < size);
    }

    public boolean equals(Board other) {
        return grid.equals(other.grid);
    }

    public ArrayList<Move> getMoveHistory() {
        return moveHistory;
    }

    public String toString() {
        String out = "";
        for (int y = size() - 1; y >= 0; y--) {
            out += y + "|";
            for (int x = 0; x < size(); x++) {
                Position p = new Position(x, y);
                AbstractChessPiece piece = getPieceAt(p);
                if (x != 0) {
                    out += "|";
                }
                if (piece != null) {
                    out += piece;
                } else {
                    if (y != size() - 1) {
                        out += "_";
                    } else {
                        out += " ";
                    }
                }
            }
            out += "\n";
        }
        out += "  A|B|C|D|E|F|G|H\n";
        return out;
    }

    /**
     * Handles the en passant move
     */
    public void enPassant() {
        if (moveHistory.size() > 2) {
            Move lastMove = moveHistory.get(moveHistory.size() - 1);
            Position lastFromPos = lastMove.getFromPos();
            Position lastToPos = lastMove.getToPos();
            AbstractChessPiece lastPiece = lastMove.getPiece();

            Move conditionMove = moveHistory.get(moveHistory.size() - 2);
            Position conditionFromPos = conditionMove.getFromPos();
            Position conditionToPos = conditionMove.getToPos();
            AbstractChessPiece conditionPiece = conditionMove.getPiece();
            if(lastPiece.getClass() == Pawn.class && conditionPiece.getClass() == Pawn.class) {
                if (conditionToPos.getX() == lastToPos.getX()) {
                    if (lastPiece.isWhite() && lastFromPos.getY() == 4 && lastToPos.getY() == 5) {
                        if (!conditionPiece.isWhite() && conditionFromPos.getY() == 6 && conditionToPos.getY() == 4) {
                            setPiece(null, conditionToPos);
                        }
                    } else if (lastFromPos.getY() == 3 && lastToPos.getY() == 2) {
                        if (conditionPiece.isWhite() && conditionFromPos.getY() == 1 && conditionToPos.getY() == 3) {
                            setPiece(null, conditionToPos);
                        }

                    }
                }
            }
        }
    }


    /** Finds out if the piece to be moved is covering the king from attack, and if so, finds the attacking pieces
     * and returns a list of positions that pieceToBeMoved could occupy without putting the king in danger (without considering valid moves)
     * @param kingpos Position of the king
     * @param pieceToBeMoved piece selected by player
     * @return A list of positions the piece can move to and still cover the king. Null if the piece isn't protecting the king.
    * */
    public ArrayList<Position> positionsPieceCanMoveToAndStillCoverKing(Position kingpos,  AbstractChessPiece pieceToBeMoved) {
           Position poseOfOtherPiece = null;
           ArrayList<Position> posesThatCanBeMovedTo= new ArrayList<Position>();
           Position posToCheck = kingpos;
            int[][] offsets = { {1,0}, {0,1}, {-1,0}, {0,-1}, {1,1}, {-1,-1}, {-1,1}, {1,-1}};
            int[] dir = new int[2];
            Position pos;
            boolean foundPieceToBeMoved=false;
            outerloop:
            for (int[] moves: offsets) {
                posToCheck = new Position(kingpos.getX() + moves[0], kingpos.getY() + moves[1]);
                while (posIsWithinBoard(posToCheck)) {
                    if (getPieceAt(posToCheck) != null) {
                        if (getPieceAt(posToCheck).equals(pieceToBeMoved)) {//Checks if there are no pieces between the king and pieceToBeMoved
                            foundPieceToBeMoved = true;
                        } else if (foundPieceToBeMoved && !getPieceAt(posToCheck).isSameColor(pieceToBeMoved) && (!dirIsHorizontal(moves) && (getPieceAt(posToCheck) instanceof Bishop) ||
                                    dirIsHorizontal(moves) && (getPieceAt(posToCheck) instanceof Rook) || (getPieceAt(posToCheck) instanceof Queen))) {
                                dir = moves;
                                poseOfOtherPiece = (posToCheck);
                                break outerloop;
                        } else {
                            foundPieceToBeMoved = false;
                            break;
                        }
                    }
                    posToCheck = new Position(posToCheck.getX() + moves[0], posToCheck.getY() + moves[1]);
                }
            }
            if (poseOfOtherPiece != null)
                return getPositionsBetween(kingpos, poseOfOtherPiece, dir, pieceToBeMoved.getPosition(this));
            return null;
    }

    private boolean dirIsHorizontal(int[] dir){
        if ((dir[0]+dir[1])%2==0)
            return false;
        return true;
    }

    /** Returns the squares between the king and the piece that will put the king in check if selected piece moves
     * @param kingPos Position of the king
     * @param pieceThatWillPutKingInCheckPos
     * @param posPieceToBeMoved Piece selected by player
     * @param dir Direction of pieceThatWillPutKingInCheckPos relative to the king
     * @return A list of positions the piece can move to and still cover the king
     * */
    public ArrayList<Position> getPositionsBetween(Position kingPos, Position pieceThatWillPutKingInCheckPos, int[] dir, Position posPieceToBeMoved){
        Position posToCheck = kingPos;
        ArrayList<Position> poses = new ArrayList<Position>();
        for (int i=1; ;i++) {
            posToCheck = new Position(kingPos.getX()+dir[0]*i, kingPos.getY()+dir[1]*i);
            if (posToCheck.equals(pieceThatWillPutKingInCheckPos))
                break;
            poses.add(posToCheck);
        }
        poses.remove(posPieceToBeMoved);
        return poses;
    }

    /** Returns the intersection of the piece's valid moves and, if the piece is covering the king, the positions it can
     * move to and still cover him
     * @param piece Piece to be moved
     * @param possibleMoves The piece's valid moves (without considering if a move will put the king in check)
     * @return A list of positions the piece can legally move to
     * */
    public ArrayList<Position> removeMovesThatWillPutOwnKingInCheck(AbstractChessPiece piece, ArrayList<Position> possibleMoves) {
        System.out.println("possible moves 1 "+possibleMoves);
        for (int i=0; i<possibleMoves.size(); i++){
            Board boardCopy = copyBoard(this);
            Position p = piece.getPosition(this);
            boardCopy.setPiece(piece, possibleMoves.get(i));
            boardCopy.setPiece(null, p);
            if (((King) getPieceAt(getKingPos(piece.isWhite()))).willThisKingBePutInCheckByMoveTo(boardCopy, getKingPos(piece.isWhite()))){
                System.out.println(possibleMoves.get(i)); possibleMoves.remove(i); i--;
            }
        }
        System.out.println("possible moves 2 "+possibleMoves);
        return possibleMoves;
    }

    public Board copyBoard(Board board){
        Board boardCopy = new Board();

        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                Position pos = new Position(i, j);
                AbstractChessPiece p = board.getPieceAt(pos);
                boardCopy.setPiece(p, pos);
            }
        }
        return boardCopy;
    }

    public int calculateBoardForActor(boolean isWhite) {
        // TODO: implement
        return 0;
    }
    /**
     * Gets all the pieces on the board.
     * @return A List of all the pieces on the board.
     */
    public List<AbstractChessPiece> getAllPieces() {
        List<AbstractChessPiece> listOfPieces = new ArrayList<AbstractChessPiece>();
        listOfPieces.addAll(getSpesificPlayersPieces(true));
        listOfPieces.addAll(getSpesificPlayersPieces(false));

        return listOfPieces;
    }

    /**
     * Gets all the pieces of a spesific player.
     * @param isWhite Which player it is.
     * @return A List of all the player's pieces.
     */
    public List<AbstractChessPiece> getSpesificPlayersPieces(boolean isWhite) {
        List<AbstractChessPiece> listOfPieces = new ArrayList<AbstractChessPiece>();

        for(int y = 0; y < size; y++) {
            for(int x = 0; x < size; x++) {
                AbstractChessPiece piece = getPieceAt(new Position(x, y));
                if(piece != null && piece.isWhite() == isWhite) {
                    listOfPieces.add(piece);
                }
            }
        }
        return listOfPieces;
    }


    }