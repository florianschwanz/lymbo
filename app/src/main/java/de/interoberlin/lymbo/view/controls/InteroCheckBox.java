package de.interoberlin.lymbo.view.controls;

import android.content.Context;
import android.widget.CheckBox;

public class InteroCheckBox extends CheckBox {
    private String text;
    private boolean right;

    public InteroCheckBox(Context context) {
        super(context);
    }

    public InteroCheckBox(Context context, String text, boolean right) {
        super(context);
        this.text = text;
        this.right = right;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}
