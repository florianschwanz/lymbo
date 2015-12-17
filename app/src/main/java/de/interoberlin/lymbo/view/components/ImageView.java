package de.interoberlin.lymbo.view.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.LinearLayout;

import java.io.File;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Image;
import de.interoberlin.lymbo.core.model.v1.objects.ImageFormat;
import de.interoberlin.lymbo.util.Base64BitmapConverter;

public class ImageView extends LinearLayout {
    // --------------------
    // Constructors
    // --------------------

    public ImageView(Context context) {
        super(context);
    }

    public ImageView(Context context, Image i) {
        super(context);
        inflate(context, R.layout.component_image, this);

        // Load layout
        android.widget.ImageView ivImage = (android.widget.ImageView) findViewById(R.id.ivImage);

        // Get Model
        String value = i.getValue();
        ImageFormat format = i.getFormat();
        File resourcePath = i.getResourcePath();

        if (format != null && value != null && !value.trim().isEmpty()) {
            switch (format) {
                case BASE_64: {
                    Bitmap bmp = Base64BitmapConverter.decodeBase64(value);
                    ivImage.setImageBitmap(bmp);
                    ivImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                    break;
                }
                case REF: {
                    String imagePath = resourcePath.getAbsolutePath() + "/" + value;

                    if (new File(imagePath).exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                        ivImage.setImageBitmap(bmp);
                        ivImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                    }
                    break;
                }
            }
        }
    }

}
