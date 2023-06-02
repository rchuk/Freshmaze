package com.dar.freshmaze.indicator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dar.freshmaze.FreshmazeGame;
import com.dar.freshmaze.level.Level;


public class Indicator extends SpriteBatch {
    private Texture bg;
    private Texture bg1;
    private Level level;
    private static final int height = 30;
    public Indicator(Level level) {
        bg = new Texture("indicator/black.png");
        bg1 = new Texture("indicator/red.png");
        this.level = level;
    }

    @Override
    public void begin() {
        super.begin();
        draw(bg, 0, FreshmazeGame.HEIGHT - height);
        draw(bg1, level.getHealth()-100, FreshmazeGame.HEIGHT - height);
    }
}
