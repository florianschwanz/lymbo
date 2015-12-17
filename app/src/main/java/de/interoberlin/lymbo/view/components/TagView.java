package de.interoberlin.lymbo.view.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.util.ColorUtil;

public class TagView extends CardView {

    // --------------------
    // Constructors
    // --------------------

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, Tag t) {
        super(context);
        inflate(context, R.layout.component_tag, this);

        TextView tvValue = (TextView) findViewById(R.id.tvValue);

        int[] colorsDark = context.getResources().getIntArray(R.array.tag_color_dark);
        int[] colorsLight = context.getResources().getIntArray(R.array.tag_color_light);

        setCardBackgroundColor(ColorUtil.getColorByString(context, t.getValue(), colorsDark, colorsLight));

        // Attribute : value
        tvValue.setTextColor(ContextCompat.getColor(context, ColorUtil.getTextColorByString(t.getValue(), colorsDark, colorsLight)));
        tvValue.setText(t.getValue());
    }
}
