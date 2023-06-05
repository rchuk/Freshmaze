package com.dar.freshmaze.indicator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class RectIndicator extends Actor {
    private Texture texture;

    private Color backgroundColor = Color.BLACK;
    private Color indicatorColor = Color.WHITE;

    private FloatRangeBinder valueBinder;

    public RectIndicator(FloatRangeBinder valueBinder) {
        super();

        texture = new Texture("rectangle.png");

        this.valueBinder = valueBinder;
    }

    public void setBackgroundColor(Color newBackgroundColor) {
        backgroundColor = newBackgroundColor;
    }

    public void setIndicatorColor(Color newIndicatorColor) {
        indicatorColor = newIndicatorColor;
    }

    // TODO: Add interpolation

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(backgroundColor);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        batch.setColor(indicatorColor);
        batch.draw(texture, getX(), getY(), getWidth() * getNormValue(), getHeight());
    }

    protected float getNormValue() {
        return valueBinder.getValue() / valueBinder.getMaxValue();
    }

    public interface FloatRangeBinder {
        float getValue();
        float getMaxValue();
    }
}
