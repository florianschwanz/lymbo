/**
 *
 */
package de.interoberlin.lymbo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import java.util.Collections;

import de.interoberlin.lymbo.controller.LymboController;
import de.interoberlin.lymbo.controller.SplashController;
import de.interoberlin.lymbo.view.controls.RobotoTextView;
import de.interoberlin.lymbo.view.activities.StacksActivity;

public class SplashActivity extends Activity {
    // Controllers
    SplashController splashController = SplashController.getInstance();
    LymboController lymboController = LymboController.getInstance();

    // Activity
    private static Activity activity;

    // Members
    private static LinearLayout ll;
    private static RobotoTextView tvMessage;
    private static boolean ready = false;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get activtiy and context for further use
        activity = this;

        ll = (LinearLayout) findViewById(R.id.ll);
        tvMessage = (RobotoTextView) findViewById(R.id.tvMessage);

        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ready) {
                    Intent openStartingPoint = new Intent(SplashActivity.this, StacksActivity.class);
                    startActivity(openStartingPoint);
                }
            }
        });

        Thread timer = new Thread() {
            public void run() {
                tvMessage.setText(R.string.search_lymbo_files);

                splashController.loadMessages();
                lymboController.getLymbos();

                Collections.shuffle(splashController.getMessages());

                uiMessage(splashController.getMessages().get(0));

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int i = 0;

                while (lymboController.getLymboFiles().isEmpty()) {
                    uiMessage(splashController.getMessages().get(i++));

                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                uiMessage("Found " + lymboController.getLymboFiles().size() + " lymbo files");

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                uiMessage(R.string.click_to_continue);

                ready = true;
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    // --------------------
    // Methods
    // --------------------

    public static void uiMessage(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessage.setText(message);
            }
        });
    }

    public static void uiMessage(final int message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessage.setText(message);
            }
        });
    }
}
