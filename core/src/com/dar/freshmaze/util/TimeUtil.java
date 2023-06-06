package com.dar.freshmaze.util;

import com.badlogic.gdx.utils.TimeUtils;

public class TimeUtil {
    private static long startTime;

    public static void init() {
        startTime = TimeUtils.millis();
    }

    public static long time() {
        return TimeUtils.timeSinceMillis(startTime);
    }

    public static float timef() {
        return time() / 1000.0f;
    }
}
