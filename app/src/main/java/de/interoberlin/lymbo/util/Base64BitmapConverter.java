package de.interoberlin.lymbo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Base64BitmapConverter {
    /**
     * Converts a Base64 String into a Bitmap
     *
     * @param input string that shall be decoded
     * @return a bitmap generated from a string
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedString = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Converts a Bitmap into a Base64 String
     *
     * @param b
     * @return a string representation of a bitmap
     */
    public static String encodeBase64(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        return Base64.encodeToString(image, Base64.DEFAULT).replace("\n", "");
    }
}