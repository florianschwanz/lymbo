package de.interoberlin.lymbo.controller.accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.view.Display;
import android.view.Surface;

import java.util.Observable;

public class Accelerator extends Observable implements SensorEventListener {
    private long shakeTime;
    private int shakeCount;

    // Shake settings
    private final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private final int SHAKE_SLOP_TIME_MS = 500;
    private final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    // Offset
    private float offsetX = 0.0F;
    private float offsetY = 0.0F;

    // Extremes
    private final float MAX_VALUE = 10.0f;
    private final float MIN_VALUE = -10.0f;

    // Factors
    private final float MAX_FACTOR = 1.0f;
    private final float MIN_FACTOR = 1.0f;

    // Get sensibilities
    private float sensibilityX = 1.0f;
    private float sensibilityY = 1.0f;

    // Measured sensor values
    float sensorX = 0.0F;
    float sensorY = 0.0F;
    float sensorZ = 0.0F;

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

        // Detect shake
        sensorX = sensorEvent.values[0];
        sensorY = sensorEvent.values[1];
        sensorZ = sensorEvent.values[2];

        float gX = sensorX / SensorManager.GRAVITY_EARTH;
        float gY = sensorY / SensorManager.GRAVITY_EARTH;
        float gZ = sensorZ / SensorManager.GRAVITY_EARTH;

        float gForce = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();
            if (shakeTime + SHAKE_SLOP_TIME_MS > now) {
                return;
            }

            if (shakeTime + SHAKE_COUNT_RESET_TIME_MS < now) {
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
