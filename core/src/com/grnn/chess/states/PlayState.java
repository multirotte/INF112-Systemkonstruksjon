package com.grnn.chess.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grnn.chess.AI.AI;
import com.grnn.chess.Board;
import com.grnn.chess.Position;
import com.grnn.chess.TranslateToCellPos;
import com.grnn.chess.objects.AbstractChessPiece;
import com.grnn.chess.objects.King;

import java.util.ArrayList;

/**
 * @author Amund 15.03.18
 */
public class PlayState extends State {

    // Variables
    Board board;
    Texture bg;
    Texture bgBoard;
    Texture potentialTex;
    Texture captureTex;

    ArrayList<Texture> pieceTexures;
    ArrayList<Position> positions;


    private Position selected;
    private ArrayList<Position> potentialMoves;
    private ArrayList<Position> captureMoves;
    private ArrayList<Position> castlingMoves;
    private TranslateToCellPos translator;
    private Boolean turn;
    private Boolean aiPlayer;
    private AI ai;
    private BitmapFont font;
    private Boolean removed;


    public PlayState(GameStateManager gsm) {
        super(gsm);
        bg = new Texture("GUI2.png");
        bgBoard = new Texture("sjakk2.png");
        pieceTexures = new ArrayList<Texture>();
        positions = new ArrayList<Position>();
        board = new Board();
        board.addPieces();
        selected = null;
        potentialMoves = new ArrayList<Position>();
        captureMoves = new ArrayList<Position>();
        castlingMoves = new ArrayList<Position>();
        translator = new TranslateToCellPos();
        turn = true;
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        removed = false;

        potentialTex = new Texture("ChessPieces/Potential.png");
        captureTex = new Texture("ChessPieces/Capture.png");


        for (int y = 40, yi = 0; y < 560; y += 65, yi++) {
            for (int x = 40, xi = 0; x < 560; x += 65, xi++) {
                AbstractChessPiece piece = board.getPieceAt(new Position(xi, yi));

                Position pos = new Position(xi, yi);
                System.out.println(pos);
                positions.add(pos);

            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bg, 0, 0);
        batch.draw(bgBoard, 0, 0);
        if (turn) {
            font.draw(batch, "Venter på at du skal gjøre neste trekk", 635, 295);
            if (removed) {
                font.draw(batch, "Datamaskinen tok en av dine brikker. FAEN I HELVETE :(", 635, 315);
            }
        } else {
            font.draw(batch, "Venter på at Datamaskin skal gjøre neste trekk", 635, 380);
            if (removed) {
                font.draw(batch, "Du tok en brikke! Bra jobbet :)", 635, 315);
            }
        }

        for (int i = 0; i < positions.size(); i++) {
            Position piecePos = positions.get(i);
            AbstractChessPiece piece = board.getPieceAt(piecePos);
            if (piece != null) {
                Texture pieceTex = new Texture(piece.getImage());
                pieceTexures.add(pieceTex);
                batch.draw(pieceTex, translator.toPixels(piecePos.getX(), piecePos.getY())[0], translator.toPixels(piecePos.getX(), piecePos.getY())[1]);
            }
        }
        if (!potentialMoves.isEmpty()) {
            for (Position potPos : potentialMoves) {
                int[] pos = translator.toPixels(potPos.getX(), potPos.getY());
                batch.draw(potentialTex, pos[0], pos[1]);
            }
        }
        if (!captureMoves.isEmpty()) {
            for (Position capPos : captureMoves) {
                int[] pos = translator.toPixels(capPos.getX(), capPos.getY());
                batch.draw(captureTex, pos[0], pos[1]);
            }
        }
        batch.end();
        if (!pieceTexures.isEmpty()) {
            for (Texture oldTexture : pieceTexures) {
                if (oldTexture.isManaged()) {
                    oldTexture.dispose();
                }
            }
        }

    }

    @Override
    public void dispose() {
        bg.dispose();
        bgBoard.dispose();
        for (Texture tex : pieceTexures) {
            tex.dispose();
        }
        potentialTex.dispose();
        captureTex.dispose();
        System.out.println("PlayState Disposed");
    }


    public void handleInput() {
        int x = Math.abs(Gdx.input.getX());
        int y = Math.abs(Gdx.input.getY());
        if (x > 0 && x < 601 && y > 0 && y < 601) {
            //first selected piece
            if (Gdx.input.justTouched() && selected == null) {
                selected = translator.toCellPos(x, y);
                AbstractChessPiece selectedPiece = board.getPieceAt(selected);
                if (selectedPiece != null && selectedPiece.isWhite() == turn) {
                    potentialMoves = selectedPiece.getValidMoves(board);
                    captureMoves = selectedPiece.getCaptureMoves(board);
                } else {
                    selected = null;
                }
            }
            //second selected piece
            else if (Gdx.input.justTouched() && selected != null) {
                Position potentialPos = translator.toCellPos(x, y);
                AbstractChessPiece potentialPiece = board.getPieceAt(potentialPos);
                Boolean valid = potentialMoves.contains(potentialPos) || captureMoves.contains(potentialPos);
                if (potentialPiece != null) {
                    if (valid) {
                        board.removePiece(potentialPiece);
                        removed = true;
                        board.movePiece(selected, potentialPos);
                        reset();
                        turn = !turn;
                    } else if (potentialPiece.isWhite() == turn) {
                        reset();
                        potentialMoves = potentialPiece.getValidMoves(board);
                        captureMoves = potentialPiece.getCaptureMoves(board);
                        selected = potentialPos;
                    } else {
                        reset();
                    }
                } else if (potentialPiece == null && valid) {
                    board.movePiece(selected, potentialPos);
                    reset();
                    turn = !turn;
                } else {
                    reset();
                }

                //first selected piece
                if (Gdx.input.justTouched() && selected == null) {
                    selected = translator.toCellPos(x, y);
                    AbstractChessPiece selectedPiece = board.getPieceAt(selected);
                    if (selectedPiece != null && selectedPiece.isWhite() == turn) {
                        potentialMoves = selectedPiece.getValidMoves(board);
                        captureMoves = selectedPiece.getCaptureMoves(board);
                        if (selectedPiece instanceof King) {
                            castlingMoves = ((King) selectedPiece).getCastlingMoves(board, selected);
                        }
                    } else {
                        selected = null;
                    }
                }
                //second selected piece
                else if (Gdx.input.justTouched() && selected != null) {
                    Boolean validMove = potentialMoves.contains(potentialPos) || captureMoves.contains(potentialPos) || castlingMoves.contains(potentialPos);
                    if (potentialPiece != null) {
                        if (validMove) {
                            board.removePiece(potentialPiece);
                            board.movePiece(selected, potentialPos);
                            reset();
                            turn = !turn;
                        } else if (potentialPiece.isWhite() == turn) {
                            reset();
                            potentialMoves = potentialPiece.getValidMoves(board);
                            captureMoves = potentialPiece.getCaptureMoves(board);
                            selected = potentialPos;
                        } else {
                            reset();
                        }
                    } else if (potentialPiece == null && validMove) {
                        handleCastling(potentialPos);
                        board.movePiece(selected, potentialPos);
                        reset();
                        turn = !turn;
                    } else {
                        reset();
                    }

                }
            }
        } else if (Gdx.input.justTouched()) {
            Gdx.app.exit();
        }


    }

    /**
     * Moves the rook if the king does castling.
     *
     * @param potentialPos The position the king is trying to move to.
     */

    private void handleCastling(Position potentialPos) {
        if (!castlingMoves.contains(potentialPos))
            return;

        AbstractChessPiece potentialPiece = board.getPieceAt(selected);
        Position rookOriginalPos = null;
        Position rookNewPos = null;

        if (potentialPiece.isWhite()) {
            if (potentialPos.equals(new Position(2, 0))) {
                rookOriginalPos = new Position(0, 0);
                rookNewPos = new Position(3, 0);
            } else if (potentialPos.equals(new Position(6, 0))) {
                rookOriginalPos = new Position(7, 0);
                rookNewPos = new Position(5, 0);
            }
        } else {
            if (potentialPos.equals(new Position(2, 7))) {
                rookOriginalPos = new Position(0, 7);
                rookNewPos = new Position(3, 7);
            } else if (potentialPos.equals(new Position(6, 7))) {
                rookOriginalPos = new Position(7, 7);
                rookNewPos = new Position(5, 7);
            }
        }
        board.movePiece(rookOriginalPos, rookNewPos);
    }

    public void reset() {
        selected = null;
        potentialMoves = new ArrayList<Position>();
        captureMoves = new ArrayList<Position>();
        castlingMoves = new ArrayList<Position>();
    }
}