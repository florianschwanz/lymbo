package de.interoberlin.lymbo.util;

import android.content.Context;

import de.interoberlin.lymbo.R;

public class ColorUtil {

    public static int getColorByString(Context c, String value) {
        int[] colors = c.getResources().getIntArray(R.array.tag_color);

        return colors[Math.abs(value.hashCode()) % colors.length];
    }
}
