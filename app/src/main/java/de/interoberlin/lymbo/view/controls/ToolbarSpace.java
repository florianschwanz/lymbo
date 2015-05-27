package de.interoberlin.lymbo.view.controls;

import android.content.Context;
import android.widget.LinearLayout;

import de.interoberlin.lymbo.R;

public class ToolbarSpace extends LinearLayout {

    // --------------------
    // Constructors
    // --------------------

    public ToolbarSpace(Context context) {
        super(context);
        inflate(context, R.layout.toolbar_space, this);
    }
}
