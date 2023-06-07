package com.dar.freshmaze.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RectangleUtil {
    public static Vector2 getRandomPoint(Rectangle rect) {
        return new Vector2(
                (int)rect.x + MathUtils.random((int)rect.width - 1),
                (int)rect.y + MathUtils.random((int)rect.height - 1)
        );
    }

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
     * Shrinks rectangle by delta in all directions
     *
     * @param rect rectangle to shrink
     * @param delta size by which to shrink the rectangle
     * @return rect
     */
    public static Rectangle shrink(Rectangle rect, Vector2 delta) {
        return expand(rect, delta.scl(-1.0f));
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

    public static boolean containsExclusive(Rectangle rect, Vector2 point) {
        return rect.x <= point.x && rect.x + rect.width > point.x &&
                rect.y <= point.y && rect.y + rect.height > point.y;
    }
}
