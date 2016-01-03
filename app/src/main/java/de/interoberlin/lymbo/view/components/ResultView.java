package de.interoberlin.lymbo.view.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import java.util.Locale;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.components.Result;

public class ResultView extends LinearLayout {

    // --------------------
    // Constructors
    // --------------------

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, Result r) {
        super(context);
        inflate(context, R.layout.component_result, this);

        android.widget.TextView tvValue = (android.widget.TextView) findViewById(R.id.tvValue);

        if (r.isCorrect()) {
            tvValue.setText(getResources().getString(R.string.correct).toUpperCase(Locale.getDefault()));
            tvValue.setTextColor(ContextCompat.getColor(context, R.color.correct));
        } else {
            tvValue.setText(getResources().getString(R.string.wrong).toUpperCase(Locale.getDefault()));
            tvValue.setTextColor(ContextCompat.getColor(context, R.color.wrong));
        }
    }

}
