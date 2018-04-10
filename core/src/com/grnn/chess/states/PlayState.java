package com.grnn.chess.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grnn.chess.*;
import com.grnn.chess.AI.AI;
import com.grnn.chess.objects.*;
//import javafx.geometry.Pos;

import java.util.ArrayList;

/**
 * @author Amund 15.03.18
 */
public class PlayState extends State {

    // Variables
    Game game;
    Board board;

    Texture bg;
    Texture bgBoard;
    Texture potentialTex;
    Texture captureTex;
    ArrayList<Texture> pieceTexures;
    ArrayList<Position> positions;


    private ArrayList<Position> potentialMoves;
    private ArrayList<Position> captureMoves;
    private ArrayList<Position> castlingMoves;
    private TranslateToCellPos translator;

    private Boolean aiPlayer;
    private AI ai;
    private Boolean activegame;
    private BitmapFont fontText;
    private BitmapFont fontCounter;

    private String text;
    private Player humanPlayer;

    private int[]removedPieces;



    public PlayState(GameStateManager gsm, boolean aiPlayer, Player player) {
        super(gsm);
        bg = new Texture("Graphics/GUI/GUI.png");
        bgBoard = new Texture("Graphics/GUI/ChessBoard.png");
        pieceTexures = new ArrayList<Texture>();
        positions = new ArrayList<Position>();
        game = new Game();
        board = game.getBoard();
        potentialMoves = game.getValidMoves();
        captureMoves = game.getCaptureMoves();
        text = game.getText();
        removedPieces = game.getRemovedPieces();

        humanPlayer = player;

        translator = new TranslateToCellPos();

        fontText = new BitmapFont();
        fontText.setColor(Color.BLACK);
        fontCounter = new BitmapFont();
        fontCounter.setColor(Color.WHITE);

        this.aiPlayer = aiPlayer;
        potentialTex = new Texture("Graphics/ChessPieces/Potential.png");
        captureTex = new Texture("Graphics/ChessPieces/Capture.png");
        activegame = true;

        if(aiPlayer){
            ai = new AI();
        }

        for( int y = 40, yi = 0; y<560; y+=65, yi++){
            for(int x=40, xi= 0; x<560; x+=65, xi++){
                AbstractChessPiece piece = board.getPieceAt(new Position(xi,yi));

                Position pos = new Position(xi, yi);
                positions.add(pos);

            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        board = game.getBoard();
        potentialMoves = game.getValidMoves();
        captureMoves = game.getCaptureMoves();
        text = game.getText();
        removedPieces = game.getRemovedPieces();
    }

    @Override
    public void render(SpriteBatch batch) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bg, 0, 0);
        batch.draw(bgBoard, 0, 0);

        if(removedPieces[2]==1){
            text = "Du vant "+ humanPlayer.name +", gratulerer!";
            activegame = false;
        }
        else if(removedPieces[8]==1){
            text = "Du vant " + humanPlayer.name + ", du må nok øve mer...";
            activegame = false;
        }

        fontText.draw(batch, text, 645, 334);
        fontCounter.draw(batch, "" + removedPieces[0], 668, 418);
        fontCounter.draw(batch, "" + removedPieces[1], 739, 418);
        fontCounter.draw(batch, "" + removedPieces[2], 810, 418);
        fontCounter.draw(batch, "" + removedPieces[3], 883, 418);
        fontCounter.draw(batch, "" + removedPieces[4], 960, 418);
        fontCounter.draw(batch, "" + removedPieces[5], 1037, 418);

        fontCounter.draw(batch, "" + removedPieces[6], 668, 105);
        fontCounter.draw(batch, "" + removedPieces[7], 739, 105);
        fontCounter.draw(batch, "" + removedPieces[8], 810, 105);
        fontCounter.draw(batch, "" + removedPieces[9], 882, 105);
        fontCounter.draw(batch, "" + removedPieces[10], 959, 105);
        fontCounter.draw(batch, "" + removedPieces[11], 1037, 105);

        // Player names
        fontCounter.draw(batch, "" + humanPlayer.getName() , 726, 241);
        fontCounter.draw(batch, "" + "Datamaskin" , 723, 555);



        for(int i=0; i<positions.size() ; i++) {
            Position piecePos = positions.get(i);
            AbstractChessPiece piece = board.getPieceAt(piecePos);
            if (piece != null) {
                Texture pieceTex = new Texture(piece.getImage());
                pieceTexures.add(pieceTex);
                batch.draw(pieceTex, translator.toPixels(piecePos.getX(), piecePos.getY())[0], translator.toPixels(piecePos.getX(), piecePos.getY())[1]);
            }
        }
        //System.out.println(potentialMoves.toString());
        if (!potentialMoves.isEmpty()) {
            System.out.println("Potentialmoves is not empty");
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



    public void handleInput() {
        int x = Math.abs(Gdx.input.getX());
        int y = Math.abs(Gdx.input.getY());
        Boolean notSelected = game.pieceHasNotBeenSelected();
        if (x>40 && x< 560 && y>40 && y<560 && activegame) {

            //AI
            //game.aiMove();
            //System.out.println(game.pieceHasNotBeenSelected());
            //first selected piece
            if (Gdx.input.justTouched() && notSelected) {
                Position selected = translator.toCellPos(x, y);
                game.selectFirstPiece(selected);
            }
            //second selected piece
            else if (Gdx.input.justTouched() && !game.pieceHasNotBeenSelected()) {
                System.out.println("moving");
                Position potentialPos = translator.toCellPos(x, y);
                game.moveFirstSelectedPieceTo(potentialPos);
            }
        }
        else if (Gdx.input.justTouched()) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        bg.dispose();
        bgBoard.dispose();
        for(Texture tex : pieceTexures){
            tex.dispose();
        }
        potentialTex.dispose();
        captureTex.dispose();
        System.out.println("PlayState Disposed");
    }

}