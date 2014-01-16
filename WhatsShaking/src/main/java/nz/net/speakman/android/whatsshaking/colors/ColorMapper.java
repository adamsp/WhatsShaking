package nz.net.speakman.android.whatsshaking.colors;

import android.graphics.Color;

/**
 * Created by Adam on 17/01/14.
 */
public class ColorMapper {
    /**
     * <p>
     * Provides a display color for a given MMI value.
     * </p>
     * <p>
     * Interpolates from greenish (for 0) through yellow, orange and red (for 10+).
     * </p>
     */
    public static int mmiColor(float mmiValue) {
        // TODO Linear interpolation is too strong, goes orange/red too early.
        // Hue from 50 (greenish) down to 0 (red)
        float hue = 50f - mmiValue * 5f;
        return Color.HSVToColor(new float[] { Math.max(0f, hue), 240f, 120f});
    }
}
