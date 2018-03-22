package com.grnn.chess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grnn.chess.states.GameStateManager;
import com.grnn.chess.states.MainMenuState;

public class ChessForKids extends ApplicationAdapter {
	GameStateManager gsm;
	SpriteBatch batch;
	Texture bg;
	Board board;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();

		bg = new Texture("Graphics/GUI/ChessBoard.png");
		board = new Board();
		board.initializeBoard();

		gsm.push(new MainMenuState(gsm));

		GameData data = new GameData();
		SerialGame hakonVsSimon = data.getGame(1);
		System.out.println(hakonVsSimon.getWhiteName());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}