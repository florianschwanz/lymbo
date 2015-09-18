package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.controller.SplashController;
import de.interoberlin.lymbo.model.accelerometer.Accelerator;
import de.interoberlin.sauvignon.lib.controller.loader.SvgLoader;
import de.interoberlin.sauvignon.lib.model.svg.SVG;
import de.interoberlin.sauvignon.lib.model.svg.elements.AGeometric;
import de.interoberlin.sauvignon.lib.model.svg.elements.rect.SVGRect;
import de.interoberlin.sauvignon.lib.model.svg.transform.transform.SVGTransformTranslate;
import de.interoberlin.sauvignon.lib.model.util.SVGPaint;
import de.interoberlin.sauvignon.lib.view.SVGSurfacePanel;

public class SplashActivity extends Activity implements Accelerator.OnTiltListener {
    public static Activity activity;

    // Controllers
    SplashController splashController;
    StacksController stacksController;

    // Views
    private static LinearLayout llSVG;
    private static LinearLayout llLogo;
    private static TextView tvMessage;

    // Accelerometer
    private SensorManager sensorManager;
    private Sensor accelerator;

    private static SVG svg;
    private static SVGSurfacePanel panel;
    private static ImageView ivLogo;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        splashController = SplashController.getInstance(this);
        stacksController = StacksController.getInstance(this);

        setContentView(R.layout.activity_splash);

        // Load layout
        llSVG = (LinearLayout) findViewById(R.id.llSVG);
        llLogo = (LinearLayout) findViewById(R.id.llLogo);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        // Get instances of managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        svg = SvgLoader.getSVGFromAsset(this, "lymbo.svg");
        panel = new SVGSurfacePanel(this);
        panel.setSVG(svg);
        panel.setBackgroundColor(new SVGPaint(255, 208, 227, 153));
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stacksController.isLoaded()) {
                    Intent openStartingPoint = new Intent(SplashActivity.this, StacksActivity.class);
                    startActivity(openStartingPoint);
                    finish();
                }
            }
        });

        ivLogo = new ImageView(this);
        ivLogo.setImageDrawable(loadFromAssets("lymbo.png"));

        // Add views
        llSVG.addView(panel, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        llLogo.addView(ivLogo, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Initial message
        tvMessage.setText(R.string.search_lymbo_files);

        Thread timer = new Thread() {
            public void run() {
                splashController.loadMessages();
                stacksController.load();

                Collections.shuffle(splashController.getMessages());

                showMessage(splashController.getMessages().get(0));

                while (!stacksController.isLoaded()) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                showMessage(getResources().getString(R.string.splash_found_1) + " " + stacksController.getStacks().size() + " " + getResources().getString(R.string.splash_found_2));

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                showMessage(R.string.click_to_continue);
            }
        };
        timer.start();

        Accelerator.getInstance().setDisplay(((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay());

        panel.init();
    }

    public void onResume() {
        super.onResume();
        panel.resume();

        Accelerator.getInstance().setOnTiltListener(this);
        sensorManager.registerListener(Accelerator.getInstance(), accelerator, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(Accelerator.getInstance());

        super.onPause();
        panel.pause();
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

    // --------------------
    // Methods - Callbacks
    // --------------------

    @Override
    public void onTilt(final float tiltX, final float tiltY) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (svg) {
                    for (AGeometric e : svg.getAllSubElements()) {
                        if (e instanceof SVGRect) {
                            float x = tiltX * (e.getzIndex() - svg.getMaxZindex() / 2) * -1.2F;
                            float y = tiltY * (e.getzIndex() - svg.getMaxZindex() / 2) * -1.2F;

                            e.getAnimationSets().clear();
                            e.setAnimationTransform(new SVGTransformTranslate(x, y));
                        }
                    }
                }
            }
        });

        t.start();
    }
}