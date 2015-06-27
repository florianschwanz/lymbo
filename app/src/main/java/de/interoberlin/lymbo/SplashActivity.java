package de.interoberlin.lymbo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.controller.SplashController;
import de.interoberlin.lymbo.controller.accelerometer.Simulation;
import de.interoberlin.lymbo.view.activities.LymbosActivity;
import de.interoberlin.sauvignon.lib.controller.loader.SvgLoader;
import de.interoberlin.sauvignon.lib.model.svg.SVG;
import de.interoberlin.sauvignon.lib.model.svg.elements.AGeometric;
import de.interoberlin.sauvignon.lib.model.svg.elements.rect.SVGRect;
import de.interoberlin.sauvignon.lib.model.svg.transform.transform.SVGTransformTranslate;
import de.interoberlin.sauvignon.lib.model.util.SVGPaint;
import de.interoberlin.sauvignon.lib.view.SVGPanel;

public class SplashActivity extends Activity {
    // Controllers
    SplashController splashController = SplashController.getInstance();
    LymbosController lymbosController = LymbosController.getInstance();

    private static Context context;
    private static Activity activity;

    // Views
    private static LinearLayout llSVG;
    private static LinearLayout llLogo;
    private static TextView tvMessage;

    private static SensorManager sensorManager;
    private WindowManager windowManager;
    private static Display display;

    private static SVG svg;
    private static SVGPanel panel;
    private static ImageView ivLogo;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get activity and context
        activity = this;
        context = getApplicationContext();

        // Load layout
        llSVG = (LinearLayout) findViewById(R.id.llSVG);
        llLogo = (LinearLayout) findViewById(R.id.llLogo);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        // Get instances of managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();

        svg = SvgLoader.getSVGFromAsset(context, "lymbo.svg");
        panel = new SVGPanel(activity);
        panel.setSVG(svg);
        panel.setBackgroundColor(new SVGPaint(255, 208, 227, 153));
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lymbosController.isLoaded()) {
                    Intent openStartingPoint = new Intent(SplashActivity.this, LymbosActivity.class);
                    startActivity(openStartingPoint);
                    finish();
                }
            }
        });

        ivLogo = new ImageView(activity);
        ivLogo.setImageDrawable(loadFromAssets("lymbo.png"));

        // Add views
        llSVG.addView(panel, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        llLogo.addView(ivLogo, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Initialize UI
        uiInit();

        // Initial message
        tvMessage.setText(R.string.search_lymbo_files);

        Thread timer = new Thread() {
            public void run() {
                splashController.loadMessages();
                lymbosController.load();

                Collections.shuffle(splashController.getMessages());

                showMessage(splashController.getMessages().get(0));

                while (!lymbosController.isLoaded()) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                showMessage(context.getResources().getString(R.string.splash_found_1) + " " + lymbosController.getLymbos().size() + " " + context.getResources().getString(R.string.splash_found_2));

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                showMessage(R.string.click_to_continue);
            }
        };
        timer.start();
    }

    public void onResume() {
        super.onResume();
        panel.resume();

        Simulation.getInstance(activity).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        panel.pause();

        Simulation.getInstance(activity).stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // --------------------
    // Methods
    // --------------------

    private Drawable loadFromAssets(String image) {
        try {
            InputStream is = getAssets().open(image);
            return Drawable.createFromStream(is, null);
        } catch (IOException ex) {
            return null;
        }
    }

    public void uiInit() {
        splashController.setOffsetX(0.0F);
        splashController.setOffsetY(-7.0F);
    }

    public static void showMessage(final int message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessage.setText(message);
            }
        });
    }

    public static void showMessage(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessage.setText(message);
            }
        });
    }

    public static void uiUpdate() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (svg) {
                    for (AGeometric e : svg.getAllSubElements()) {
                        if (e instanceof SVGRect) {
                            float x = Simulation.getRawX() * (e.getzIndex() - svg.getMaxZindex() / 2) * -1.2F;
                            float y = Simulation.getRawY() * (e.getzIndex() - svg.getMaxZindex() / 2) * -1.2F;

                            e.getAnimationSets().clear();
                            e.setAnimationTransform(new SVGTransformTranslate(x, y));
                        }
                    }
                }
            }
        });

        t.start();
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public Display getDisplay() {
        return display;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }
}