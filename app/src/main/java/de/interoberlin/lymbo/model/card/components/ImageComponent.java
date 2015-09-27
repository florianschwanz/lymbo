package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.Displayable;
import de.interoberlin.lymbo.model.card.EImageFormat;
import de.interoberlin.lymbo.util.Base64BitmapConverter;

public class ImageComponent implements Displayable {
    private String value;
    private EImageFormat format = EImageFormat.BASE64;

    private File resourcePath;

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

        if (format != null && value != null && !value.trim().isEmpty()) {
            switch (format) {
                case BASE64: {
                    Bitmap bmp = Base64BitmapConverter.decodeBase64(value);
                    ivImage.setImageBitmap(bmp);
                    ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                }
                case REF: {
                    String imagePath = resourcePath.getAbsolutePath() + "/" + value;

                    if (new File(imagePath).exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                        ivImage.setImageBitmap(bmp);
                        ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    break;
                }
            }
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

    public EImageFormat getFormat() {
        return format;
    }

    public void setFormat(EImageFormat format) {
        this.format = format;
    }

    public File getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(File resourcePath) {
        this.resourcePath = resourcePath;
    }
}