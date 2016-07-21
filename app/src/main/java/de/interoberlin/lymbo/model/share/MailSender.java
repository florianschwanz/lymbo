package de.interoberlin.lymbo.model.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;

public class MailSender {
    // --------------------
    // Methods
    // --------------------

    /**
     * Sends a stack as a mail attachment
     *
     * @param context  context
     * @param activity activity
     * @param stack    stack
     */
    public static void sendLymbo(Context context, Activity activity, Stack stack) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        File file = new File(stack.getPath());
        if (!file.exists() || !file.canRead()) {
            Toast.makeText(activity, "Attachment Error", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }
        Uri uri = Uri.parse("file://" + file);

        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.lymbo_for_you) + " " + stack.getTitle());
        emailIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.download_the_attachment));
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        activity.startActivity(Intent.createChooser(emailIntent, context.getResources().getString(R.string.send)));
    }
}
