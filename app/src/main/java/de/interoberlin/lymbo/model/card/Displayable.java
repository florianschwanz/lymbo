package de.interoberlin.lymbo.model.card;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface Displayable {
    public View getView(Context c, final Activity a, ViewGroup parent);

    public View getEditableView(Context c, final Activity a, ViewGroup parent);
}
