package com.dar.freshmaze.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RectangleUtil {
    /**
     * Expands rectangle by delta in all directions
     *
     * @param rect rectangle to expand
     * @param delta size by which to expand the rectangle
     * @return rect
     */
    public static Rectangle expand(Rectangle rect, Vector2 delta) {
        return new Rectangle(
                rect.x - delta.x,
                rect.y - delta.y,
                rect.width + 2.0f * delta.x,
                rect.height + 2.0f * delta.y
        );
    }

    /**
     * Normalizes a rectangle, in other words,
     * makes sure it has strictly positive size
     * and modifies its position accordingly
     *
     * @param rect rectangle to normalize
     * @return rect
     */
    public static Rectangle normalize(Rectangle rect) {
        if (rect.width < 0) {
            rect.width = -rect.width;
            rect.x -= rect.width;
        }
        if (rect.height < 0) {
            rect.height = -rect.height;
            rect.y -= rect.height;
        }

        return rect;
    }
}
