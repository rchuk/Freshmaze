package com.dar.freshmaze.common;

import java.util.Random;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CommonHelper {
    public static Vector2 randomPoint(Rectangle r) {
        Random rand = new Random();
        return new Vector2(rand.nextInt((int) (r.getWidth())) + r.getX(), rand.nextInt((int) (r.getHeight())) + r.getY());
    }
}
