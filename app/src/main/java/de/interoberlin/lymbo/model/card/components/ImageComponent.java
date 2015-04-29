package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.util.Base64BitmapConverter;

public class ImageComponent implements Displayable {
    private String value;

    private boolean flip = false;

    // --------------------
    // Constructor
    // --------------------

    public ImageComponent() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llImageComponent = (LinearLayout) li.inflate(R.layout.component_image, parent, false);

        ImageView ivImage = (ImageView) llImageComponent.findViewById(R.id.ivImage);

        if (value != null) {
            Bitmap b = Base64BitmapConverter.decodeBase64(value);
            BitmapDrawable bd = new BitmapDrawable(b);
            ivImage.setBackgroundDrawable(bd);

            // LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (maxHeight * ratio), (int) (maxHeight));
            // layoutParams.gravity = Gravity.CENTER;
            // ivImage.setLayoutParams(layoutParams);
        }

        return llImageComponent;
    }

    @Override
    public View getEditableView(Context c, final Activity a, ViewGroup parent) {
        return new View(c);
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}