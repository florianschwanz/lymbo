package de.interoberlin.lymbo.util;

import android.content.Context;

import org.apache.commons.lang3.ArrayUtils;

import de.interoberlin.lymbo.R;

public class ColorUtil {

    /**
     * Returns a color from a given set of possibilities based on the string content
     *
     * @param c           context
     * @param value       string value
     * @param colorsDark  array of dark colors
     * @param colorsLight array of light colors
     * @return generated color
     */
    public static int getColorByString(Context c, String value, int[] colorsDark, int[] colorsLight) {
        int[] colors = ArrayUtils.addAll(colorsDark, colorsLight);

        return colors[Math.abs(value.hashCode()) % (colors.length)];
    }

    /**
     * Returns a suitable text color for given set of possibilities
     *
     * @param value       string value
     * @param colorsDark  array of dark colors
     * @param colorsLight array of light colors
     * @return suitable color
     */
    public static int getTextColorByString(String value, int[] colorsDark, int[] colorsLight) {
        return Math.abs(value.hashCode()) % (colorsDark.length + colorsLight.length) < colorsDark.length ? R.color.white : R.color.card_title_text;
    }
}
