package com.dar.freshmaze.util;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * Class with various helper methods for
 * woring with isometric coordinate system
 */
public class IsometricUtil {
    /** Matrix that can be used to transform coordinates to isometric for rendering (ot other purposes).
     * Doesn't need to be used if the sprites are already drawn as isometric.
     *
     * Can be used like:
     * <pre>
     * {@code game.batch.setTransformMatrix(IsometricUtil.ISO_TRANSFORMATION_MATRIX); }
     * </pre>
     * or
     * <pre>
     * {@code physDebugRenderer.render(physWorld, camera.combined.mul(IsometricUtil.ISO_TRANSFORMATION_MATRIX)); }
     * </pre>
     *
     */
    public static final Matrix4 ISO_TRANSFORMATION_MATRIX;

    static {
        ISO_TRANSFORMATION_MATRIX = new Matrix4()
            .idt()
            .scale((float)(Math.sqrt(2.0) / 2.0), (float)(Math.sqrt(2.0) / 4.0), 1.0f)
            .rotate(0.0f, 0.0f, 1.0f, -45);
    }

    /**
     * Converts vector from isometric to cartesian coordinate system.
     *
     * The original formula is the following:
     * <pre>
     * {@code
     * return new Vector2(
     *      0.5f * (2.0f * vec.y + vec.x),
     *      0.5f * (2.0f * vec.y - vec.x)
     * );
     * }
     * </pre>
     * But we need to rotate it to match our tilemap. The result is:
     * <pre>
     * {@code
     *  return new Vector2(
     *      0.5f * (-2.0f * vec.y + vec.x),
     *      0.5f * (2.0f * vec.y + vec.x)
     *  );
     *  }
     *  </pre>
     *
     * @param vec vector in isometric system
     * @return vector in cartesian system
     */
    public static Vector2 isoToCart(Vector2 vec) {
        return new Vector2(
                0.5f * (-2.0f * vec.y + vec.x),
                0.5f * (2.0f * vec.y + vec.x)
        );
    }

    /**
     * Converts vector from cartesian to isometric coordinate system.
     *
     * The original formula is the following:
     * <pre>
     * {@code
     * return new Vector2(
     *      vec.x - vec.y,
     *      0.5f * (vec.x + vec.y)
     * );
     * }
     * </pre>
     * But we need to rotate it to match our tilemap. The result is:
     * <pre>
     * {@code
     *  return new Vector2(
     *      vec.x + vec.y,
     *      0.5f * (vec.x - vec.y)
     *  );
     *  }
     *  </pre>
     *
     *  NOTE: I have no idea where multiplication of both
     *  x and y by 0.5 comes from, it makes no sense. Fix it if possible
     *
     * @param vec vector in cartesian system
     * @return vector in isometric system
     */
    public static Vector2 cartToIso(Vector2 vec) {
        return new Vector2(
                0.5f * (vec.x + vec.y),
                0.25f * (vec.y - vec.x)
        );
    }
}
