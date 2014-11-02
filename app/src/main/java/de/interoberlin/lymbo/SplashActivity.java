/**
 *
 */
package de.interoberlin.lymbo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.interoberlin.lymbo.view.activities.HomeActivity;
import de.interoberlin.lymbo.view.activities.RobotoTextView;
import de.interoberlin.mate.lib.model.Log;

public class SplashActivity extends Activity {
    // private static WebView wvLogo;
    private static LinearLayout ll;
    private static RobotoTextView tvMessage;

    private static List<String> messages = new ArrayList<String>();
    private static boolean ready = false;

    private static Activity activity;

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get activtiy and contex for further use
        activity = this;

        // wvLogo = (WebView) findViewById(R.id.wvLogo);
        ll = (LinearLayout) findViewById(R.id.ll);
        tvMessage = (RobotoTextView) findViewById(R.id.tvMessage);

        tvMessage.setText("Search lymbo files");

        ll.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.splash));

        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ready) {
                    Intent openStartingPoint = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(openStartingPoint);
                }
            }
        });

        Thread timer = new Thread() {
            public void run() {
                loadMessages();
                findLymboFiles();

                Collections.shuffle(messages);

                uiMessage(messages.get(0));

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int i = 0;

            /*
        while (Properties.getLymboFiles().isEmpty())
		{

		    uiMessage(messages.get(i++));

		    try
		    {
			sleep(500);
		    } catch (InterruptedException e)
		    {
			e.printStackTrace();
		    }
		}

		uiMessage("Found " + Properties.getLymboFiles().size() + " lymbo files");
*/

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                uiMessage("Click on screen to continue");

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

    private static void loadMessages() {
        for (int i = 0; i < EMessage.values().length; i++) {
            messages.add(EMessage.values()[i].getText());
        }
    }

    private static void findLymboFiles() {
        /*
	Log.trace("SplashActivity.findLymboFiles()");
	Properties.setLymboFiles(findFiles(".lymbo"));
	*/
    }

    public static Collection<File> findFiles(String pattern) {
        Log.trace("StackActivity.findFiles()");
        return FileUtils.listFiles(Environment.getExternalStorageDirectory(), new RegexFileFilter(".*" + pattern), TrueFileFilter.TRUE);
    }

    public static void uiMessage(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessage.setText(message);
            }
        });
    }
}
