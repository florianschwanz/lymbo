package de.interoberlin.lymbo.view.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
    public static final String TAG = SplashActivity.class.getSimpleName();

    // Model
    private static SVG svg;

    // Views
    private static LinearLayout llSVG;
    private static LinearLayout llLogo;
    private static TextView tvMessage;
    private static SVGSurfacePanel panel;
    private static ImageView ivLogo;

    // Controller
    SplashController splashController;
    StacksController stacksController;

    // Accelerometer
    private SensorManager sensorManager;
    private Sensor accelerator;

    // Properties
    private static final int  PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int  PERMISSION_REQUEST_VIBRATE = 1;
    private static final int  PERMISSION_REQUEST_INTERNET = 2;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        requestPermission(Manifest.permission.VIBRATE, PERMISSION_REQUEST_VIBRATE);
        requestPermission(Manifest.permission.INTERNET, PERMISSION_REQUEST_INTERNET);

        splashController = SplashController.getInstance();
        stacksController = StacksController.getInstance();

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
                stacksController.load(SplashActivity.this);

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

    /**
     * Asks user for permission
     *
     * @param permission permission to ask for
     * @param callBack   callback
     */
    private void requestPermission(String permission, int callBack) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permission not granted");

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        callBack);
            }
        } else {
            Log.i(TAG, "Permission granted");
        }
    }

    private Drawable loadFromAssets(String image) {
        try {
            InputStream is = getAssets().open(image);
            return Drawable.createFromStream(is, null);
        } catch (IOException ex) {
            return null;
        }
    }

    public void showMessage(final int message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessage.setText(message);
            }
        });
    }

    public void showMessage(final String message) {
        runOnUiThread(new Runnable() {
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