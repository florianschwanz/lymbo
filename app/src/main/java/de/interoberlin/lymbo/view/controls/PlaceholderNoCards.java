package de.interoberlin.lymbo.view.controls;

import android.content.Context;
import android.widget.LinearLayout;

import de.interoberlin.lymbo.R;

public class PlaceholderNoCards extends LinearLayout {

    // --------------------
    // Constructors
    // --------------------

    public PlaceholderNoCards(Context context) {
        super(context);
        inflate(context, R.layout.placeholder_no_cards, this);
    }
}
