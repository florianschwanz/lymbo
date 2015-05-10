package de.interoberlin.lymbo.model.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.Lymbo;

public class MailSender {
    // --------------------
    // Methods
    // --------------------

    /**
     * Uses an intent
     * @param c
     * @param a
     * @param lymbo
     */
    public static void sendLymbo(Context c, Activity a, Lymbo lymbo) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        File file = new File(lymbo.getPath());
        if (!file.exists() || !file.canRead()) {
            Toast.makeText(a, "Attachment Error", Toast.LENGTH_SHORT).show();
            a.finish();
            return;
        }
        Uri uri = Uri.parse("file://" + file);

        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, c.getResources().getString(R.string.lymbo_for_you) + " " + lymbo.getTitle());
        emailIntent.putExtra(Intent.EXTRA_TEXT, c.getResources().getString(R.string.download_the_attachment));
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        a.startActivity(Intent.createChooser(emailIntent, c.getResources().getString(R.string.send)));
    }
}