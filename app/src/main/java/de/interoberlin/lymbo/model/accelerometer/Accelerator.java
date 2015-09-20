package de.interoberlin.lymbo.model.accelerometer;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;

import java.util.Observable;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;

public class Accelerator extends Observable implements SensorEventListener {
    private long shakeTime;
    private int shakeCount;
    private Display display;

    // Listeners
    private OnShakeListener onShakeListener;
    private OnTiltListener onTiltListener;

    private static Accelerator instance;

    // --------------------
    // Constructors
    // --------------------

    private Accelerator() {
    }

    public static Accelerator getInstance() {
        if (instance == null) {
            instance = new Accelerator();
        }

        return instance;
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        Resources res = App.getContext().getResources();

        int SHAKE_THRESHOLD_GRAVITY = res.getInteger(R.integer.shake_threshold_gravity);
        int SHAKE_SLOP_TIME = res.getInteger(R.integer.shake_slop_time);
        int SHAKE_COUNT_RESET_TIME = res.getInteger(R.integer.shake_count_reset_time);

        // Offset
        float offsetX = 0.0F;
        float offsetY = 0.0F;

        // Get sensibilities
        float sensibilityX = 1.0f;
        float sensibilityY = 1.0f;

        // Detect shake
        float sensorX = sensorEvent.values[0];
        float sensorY = sensorEvent.values[1];
        float sensorZ = sensorEvent.values[2];

        float gX = sensorX / SensorManager.GRAVITY_EARTH;
        float gY = sensorY / SensorManager.GRAVITY_EARTH;
        float gZ = sensorZ / SensorManager.GRAVITY_EARTH;

        double gForce = Math.sqrt(Math.pow(gX, 2) + Math.pow(gY, 2) + Math.pow(gZ, 2));

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();
            if (shakeTime + SHAKE_SLOP_TIME > now) {
                return;
            }

            if (shakeTime + SHAKE_COUNT_RESET_TIME < now) {
                shakeCount = 0;
            }

            shakeTime = now;
            shakeCount++;

            if (onShakeListener != null)
                onShakeListener.onShake(shakeCount);
        }

        // Detect tilt
        if (display != null) {
            switch (display.getRotation()) {
                case Surface.ROTATION_0: {
                    sensorX = sensorEvent.values[0];
                    sensorY = -sensorEvent.values[1];
                    break;
                }
                case Surface.ROTATION_90: {
                    sensorX = -sensorEvent.values[1];
                    sensorY = -sensorEvent.values[0];
                    break;
                }
                case Surface.ROTATION_180: {
                    sensorX = -sensorEvent.values[0];
                    sensorY = sensorEvent.values[1];
                    break;
                }
                case Surface.ROTATION_270: {
                    sensorX = sensorEvent.values[1];
                    sensorY = sensorEvent.values[0];
                    break;
                }
            }
        }

        if (onTiltListener != null)
            onTiltListener.onTilt(normalize(sensorX - offsetX, sensibilityX), normalize(sensorY - offsetY, sensibilityY));

        setChanged();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    // --------------------
    // Methods
    // --------------------

    private float normalize(float f, float sensibility) {
        // Factors
        float MAX_FACTOR = 1.0f;
        float MIN_FACTOR = 1.0f;

        // Extremes
        float MAX_VALUE = 10.0f;
        float MIN_VALUE = -10.0f;

        float FACTOR = (((MAX_FACTOR - MIN_FACTOR) / 100) * sensibility) + MIN_FACTOR;

        if (f * FACTOR > MAX_VALUE) {
            return MAX_VALUE;
        } else if (f * FACTOR < MIN_VALUE) {
            return MIN_VALUE;
        } else {
            return f * FACTOR;
        }
    }

    public void setOnShakeListener(OnShakeListener onShakeListener) {
        this.onShakeListener = onShakeListener;
    }

    public void setOnTiltListener(OnTiltListener onTiltListener) {
        this.onTiltListener = onTiltListener;
    }

    public interface OnShakeListener {
        void onShake(int count);
    }

    public interface OnTiltListener {
        void onTilt(float x, float y);
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public void setDisplay(Display display) {
        this.display = display;
    }
}
