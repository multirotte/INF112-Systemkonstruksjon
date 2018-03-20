package com.grnn.chess;
import com.grnn.chess.objects.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class to represent a board
 */

public class Board {

    // Variables
    private int size = 8;
    private ArrayList<ArrayList<AbstractChessPiece>> grid = new ArrayList<ArrayList<AbstractChessPiece>>(size);
    private ArrayList<AbstractChessPiece> removedPieces;
    private ArrayList<Move> moveHistory;

    public Board() {
        moveHistory = new ArrayList<Move>();
        removedPieces = new ArrayList<AbstractChessPiece>();
        for(int i = 0; i < size; i++) {
            grid.add(new ArrayList<AbstractChessPiece>(size));
            for (int j = 0; j < size; j++)
                grid.get(i).add(null);
        }
    }
    public void removePiece(AbstractChessPiece piece){
        removedPieces.add(piece);
    }

    public ArrayList<AbstractChessPiece> getRemovedPieces() {
        return removedPieces;
    }

    public void movePiece(Position startPos, Position endPos) {
        AbstractChessPiece piece = getPieceAt(startPos);

        //TODO :assert that the move is valid

        setPiece(piece, endPos);
        setPiece(null, startPos);
        piece.move();
        moveHistory.add(new Move(endPos, startPos, piece));
        enPassant();
    }
    // TODO: AI is not always black
    public ArrayList<Move> getPossibleAIMoves() { //Aka get black's moves

        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        for(int y = 6; y <= 7; y++) {
            for(int x = 0; x < size(); x++) {
                Position posPiece = new Position(x, y);
                AbstractChessPiece piece = getPieceAt(posPiece);
                ArrayList<Position> posList = piece.getValidMoves(this);

                for (Position toMove : posList) {
                    Move newMove = new Move(toMove, posPiece, piece);
                    possibleMoves.add(newMove);
                    System.out.println(newMove);
                }
            }
        }
        return possibleMoves;
    }


    public Board updateBoard(){
        return null;
    }

    public int size() {
        return grid.size();
    }

    public void addPieces() {


        for(int i = 0; i < size(); i++) {
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
        grid.get(y).set(x, piece);
    }

    public AbstractChessPiece getPieceAt(Position p) {
        if(posIsWithinBoard(p)) {
            return grid.get(p.getY()).get(p.getX());
        }
        return null;
    }

    public Position getPosition(AbstractChessPiece piece) {
        for(int y = 0; y < size(); y++) {
            for(int x = 0; x < size(); x++) {
                Position p = new Position(x, y);
                if(getPieceAt(p) != null && getPieceAt(p).equals(piece))
                    return p;
            }
        }
        return null;
    }

    public boolean posIsWithinBoard(Position pos){
        return (pos.getX()>=0 && pos.getX()< size && pos.getY()>=0 && pos.getY()< size );
    }

    public boolean equals(Board other){
        return grid.equals(other.grid);
    }

    public ArrayList<Move> getMoveHistory(){
        return moveHistory;
    }

    public String toString() {
        String out = "";
        for(int y = size - 1; y >= 0; y--) {
            out += y + "|";
            for(int x = 0; x < size(); x++) {
                Position p = new Position(x, y);
                AbstractChessPiece piece = getPieceAt(p);
                if(x != 0) {
                    out += "|";
                }
                if(piece != null) {
                    out += piece;
                } else {
                    if(y != size() - 1) {
                        out += "_";
                    }else{
                        out += " ";
                    }
                }
            }
            out += "\n";
        }
        out += "  A|B|C|D|E|F|G|H\n";
        return out;
    }

    public void enPassant(){
        if(moveHistory.size()>2) {
            Move lastMove = moveHistory.get(moveHistory.size()-1);
            Position lastFromPos = lastMove.getFromPos();
            Position lastToPos = lastMove.getToPos();
            AbstractChessPiece lastPiece = lastMove.getPiece();

            Move conditionMove = moveHistory.get(moveHistory.size()-2);
            Position conditionFromPos = conditionMove.getFromPos();
            Position conditionToPos = conditionMove.getToPos();
            AbstractChessPiece conditionPiece = conditionMove.getPiece();

            if(conditionToPos.getX()==lastToPos.getX()+1 || conditionToPos.getX()==lastToPos.getX()-1)
                if(lastPiece.getColor() && lastFromPos.getY()==4 && lastToPos.getY()==5){
                    if(!conditionPiece.getColor() && conditionFromPos.getY()==6 && conditionToPos.getY()==4){
                        setPiece(null,conditionToPos);
                    }
                }else{
                    if(conditionPiece.getColor() && conditionFromPos.getY()==1 && conditionToPos.getY()==3){
                        setPiece(null, conditionToPos);
                    }

                }
        }
    }
}