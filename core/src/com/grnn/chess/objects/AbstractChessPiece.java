package com.grnn.chess.objects;


import com.grnn.chess.Board;
import com.grnn.chess.Position;

import java.util.ArrayList;

/**
 * Abstract class to represent a chess piece
 */
public abstract class AbstractChessPiece {
    protected boolean isWhite;
    protected boolean validMove;
    protected ArrayList<Position> validMoves;
    protected ArrayList<Position> captureMoves;
    protected String letterRepresentation = "";

    //private Direction askedToGo;

    /*public Position getPosition(){

    }*/

    public AbstractChessPiece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean getColor(){
        return isWhite;
    }

    public Position getPosition(Board board) {
        return board.getPosition(this);
    }

    public boolean equals(AbstractChessPiece otherPiece) {
        return this.hashCode() == otherPiece.hashCode();
    }

    public ArrayList<Position> getValidMoves() {
        return new ArrayList<Position>();
    }

    public ArrayList<Position> getCaptureMoves() {
        return new ArrayList<Position>();
    }

    public String toString() {
		return isWhite ? letterRepresentation : letterRepresentation.toUpperCase();
	}
}




