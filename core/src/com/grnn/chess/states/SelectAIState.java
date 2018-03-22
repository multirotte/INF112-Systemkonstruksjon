package com.grnn.chess.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;

/**
 * @author Helge Mikael Landro, 19.03.2018
 * A class to represent the menu for select AI level for the game.
 * The user can choose from three levels of difficulty.
 */
public class SelectAIState extends State {

    // Variables
    private Texture background, pieces;
    private Texture playBtn, playBtn2, playBtn3, playBtn4;
    private Texture emoticonEasy, emoticonEasy2, emoticonMedium, emoticonMedium2, emoticonHard, emoticonHard2;
    private int xPlay, yPlay, count;
    private ArrayList<Texture> test;

    /**
     * Constructor for the AI State
     * @param gsm, the GameStateManager
     */
    public SelectAIState(GameStateManager gsm){
        super(gsm);
        background = new Texture("Menu/AI_menu.png");
        pieces = new Texture("Menu/Menu_pieces.png");
        playBtn = new Texture("Menu/menu_button_AI_easy.png");
        playBtn2 = new Texture("Menu/menu_button_AI_medium.png");
        playBtn3 = new Texture("Menu/menu_button_AI_hard.png");
        playBtn4 = new Texture("Menu/menu_button_venn.png");
        emoticonEasy = new Texture("Menu/Animations/Emoji_1/1.png");
        emoticonEasy2 = new Texture("Menu/Animations/Emoji_1/2.png");
        emoticonMedium = new Texture("Menu/Animations/Emoji_2/3.png");
        emoticonMedium2 = new Texture("Menu/Animations/Emoji_2/4.png");
        emoticonHard = new Texture("Menu/Animations/Emoji_3/5.png");
        emoticonHard2 = new Texture("Menu/Animations/Emoji_3/6.png");
        xPlay = 300;
        yPlay = 350;
        count = 0;
        test = new ArrayList<Texture>();
        test.add(emoticonEasy);
        test.add(emoticonEasy2);
        test.add(emoticonMedium);
        test.add(emoticonMedium2);
        test.add(emoticonHard);
        test.add(emoticonHard2);
    }

    /**
     * Method to handle inputs from the mouse
     */
    public void handleInput() {
        int x = Math.abs(Gdx.input.getX());
        int y = Math.abs(Gdx.input.getY()-Gdx.graphics.getHeight());
        int texturePosX = xPlay;
        int  texturePosY = yPlay;
        if (x > texturePosX && y > texturePosY && x < playBtn.getWidth()+texturePosX && y < playBtn.getHeight()+texturePosY && Gdx.input.justTouched()) {
            gsm.set(new PlayState(gsm, true));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0,0);
        sb.draw(pieces, 70, 0);
        sb.draw(playBtn, xPlay, yPlay);
        sb.draw(playBtn2, xPlay, yPlay - 70);
        sb.draw(playBtn3, xPlay, yPlay - 140);
        sb.draw(playBtn4, xPlay+290, yPlay );

        count++;

        // Draw emojis
        if(count  < 15 ) {
            sb.draw(test.get(0), xPlay-30, yPlay);
            sb.draw(test.get(4), xPlay-17, yPlay-88);
            sb.draw(test.get(2), xPlay-10, yPlay-134);

        } else{
            sb.draw(test.get(1), xPlay-30, yPlay);
            sb.draw(test.get(5), xPlay-17, yPlay-88);
            sb.draw(test.get(3), xPlay-10, yPlay-134);

            if(count  == 30)
                count = 0;
        }
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtn.dispose();
        playBtn2.dispose();
        playBtn3.dispose();
        emoticonEasy.dispose();
        emoticonEasy2.dispose();
        System.out.println("SelectAI State Disposed");
    }
}