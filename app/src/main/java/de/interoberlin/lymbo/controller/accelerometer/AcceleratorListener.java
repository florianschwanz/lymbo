package de.interoberlin.lymbo.controller.accelerometer;

import android.app.Activity;

import java.util.Observable;
import java.util.Observer;

public class AcceleratorListener implements Observer {
    private static AcceleratorListener instance;

    private static final float MAX_VALUE = 10.0f;
    private static final float MIN_VALUE = 10.0f;

    // Data values
    private static float dataX = Float.MAX_VALUE;
    private static float dataY = Float.MAX_VALUE;

    // Measured values + offset + rounded
    private static int x = Integer.MAX_VALUE;
    private static int y = Integer.MAX_VALUE;

    private Activity activity;

    private AcceleratorListener(Activity activity) {
        this.activity = activity;
    }

    public static AcceleratorListener getInstance(Activity activity) {
        if (instance == null) {
            instance = new AcceleratorListener(activity);
        }

        return instance;
    }

    @Override
    public void update(Observable observable, Object data) {
        // Get acceleration data
        dataX = ((AccelerationEvent) data).getX();
        dataY = ((AccelerationEvent) data).getY();

        if (dataX < MIN_VALUE)
            dataX = MIN_VALUE;
        if (dataX > MAX_VALUE)
            dataX = MAX_VALUE;
        if (dataY < MIN_VALUE)
            dataY = MIN_VALUE;
        if (dataY > MAX_VALUE)
            dataY = MAX_VALUE;

        x = Math.round(dataX);
        y = Math.round(dataY);

    }

    public void start() {
        Accelerometer.getInstance(activity).start();
    }

    public void stop() {
        Accelerometer.getInstance(activity).stop();
    }

    public static int getX() {
        return x;
    }

    public static void setX(int x) {
        AcceleratorListener.x = x;
    }

    public static int getY() {
        return y;
    }

    public static void setY(int y) {
        AcceleratorListener.y = y;
    }

    public static float getDataX() {
        return dataX;
    }

    public static float getDataY() {
        return dataY;
    }
}
