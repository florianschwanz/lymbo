package de.interoberlin.lymbo.controller.accelerometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;

import java.util.Observable;

import de.interoberlin.sugarmonkey.controller.Simulation;
import de.interoberlin.sugarmonkey.view.activities.examples.InteroberlinActivity;
import de.interoberlin.sugarmonkey.view.activities.examples.LevelActivity;
import de.interoberlin.sugarmonkey.view.activities.examples.LymboActivity;
import de.interoberlin.sugarmonkey.view.activities.examples.StomachionActivity;

public class Accelerometer extends Observable implements SensorEventListener {
    private Activity activity;

    private Sensor accelerometer;
    private float sensorX;
    private float sensorY;

    private static Accelerometer instance;

    private Accelerometer(Activity activity) {
        if (activity instanceof LymboActivity)
            accelerometer = ((LymboActivity) activity).getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (activity instanceof InteroberlinActivity)
            accelerometer = ((InteroberlinActivity) activity).getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (activity instanceof StomachionActivity)
            accelerometer = ((StomachionActivity) activity).getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        this.activity = activity;

        addObserver(Simulation.getInstance(activity));
    }

    public static Accelerometer getInstance(Activity activity) {
        if (instance == null) {
            instance = new Accelerometer(activity);
        }

        return instance;
    }

    public void start() {
        if (activity instanceof LymboActivity) {
            LymboActivity.uiToast("Accelerometer started");
            ((LymboActivity) activity).getSensorManager().registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (activity instanceof InteroberlinActivity) {
            InteroberlinActivity.uiToast("Accelerometer started");
            ((InteroberlinActivity) activity).getSensorManager().registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (activity instanceof StomachionActivity) {
            StomachionActivity.uiToast("Accelerometer started");
            ((StomachionActivity) activity).getSensorManager().registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        if (activity instanceof LymboActivity) {
            LymboActivity.uiToast("Accelerometer stopped");
            ((LymboActivity) activity).getSensorManager().unregisterListener(this);
        }
        if (activity instanceof LevelActivity) {
            LevelActivity.uiToast("Accelerometer stopped");
            ((LevelActivity) activity).getSensorManager().unregisterListener(this);
        }
        if (activity instanceof StomachionActivity) {
            StomachionActivity.uiToast("Accelerometer stopped");
            ((StomachionActivity) activity).getSensorManager().unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        if (activity instanceof LymboActivity) {
            switch (((LymboActivity) activity).getDisplay().getRotation()) {
                case Surface.ROTATION_0: {
                    sensorX = event.values[0];
                    sensorY = -event.values[1];
                    break;
                }
                case Surface.ROTATION_90: {
                    sensorX = -event.values[1];
                    sensorY = -event.values[0];
                    break;
                }
                case Surface.ROTATION_180: {
                    sensorX = -event.values[0];
                    sensorY = event.values[1];
                    break;
                }
                case Surface.ROTATION_270: {
                    sensorX = event.values[1];
                    sensorY = event.values[0];
                    break;
                }
            }
        }

        if (activity instanceof InteroberlinActivity) {
            switch (((InteroberlinActivity) activity).getDisplay().getRotation()) {
                case Surface.ROTATION_0: {
                    sensorX = event.values[0];
                    sensorY = -event.values[1];
                    break;
                }
                case Surface.ROTATION_90: {
                    sensorX = -event.values[1];
                    sensorY = -event.values[0];
                    break;
                }
                case Surface.ROTATION_180: {
                    sensorX = -event.values[0];
                    sensorY = event.values[1];
                    break;
                }
                case Surface.ROTATION_270: {
                    sensorX = event.values[1];
                    sensorY = event.values[0];
                    break;
                }
            }
        }

        if (activity instanceof StomachionActivity) {
            switch (((StomachionActivity) activity).getDisplay().getRotation()) {
                case Surface.ROTATION_0: {
                    sensorX = event.values[0];
                    sensorY = -event.values[1];
                    break;
                }
                case Surface.ROTATION_90: {
                    sensorX = -event.values[1];
                    sensorY = -event.values[0];
                    break;
                }
                case Surface.ROTATION_180: {
                    sensorX = -event.values[0];
                    sensorY = event.values[1];
                    break;
                }
                case Surface.ROTATION_270: {
                    sensorX = event.values[1];
                    sensorY = event.values[0];
                    break;
                }
            }
        }

        setChanged();

        notifyObservers(new AccelerationEvent(sensorX, sensorY));

        if (activity instanceof LymboActivity) {
            LymboActivity.uiUpdate();
            LymboActivity.uiDraw();
        }

        if (activity instanceof InteroberlinActivity) {
            InteroberlinActivity.uiUpdate();
        }

        if (activity instanceof StomachionActivity) {
            StomachionActivity.uiUpdate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}