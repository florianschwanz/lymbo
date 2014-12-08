package de.interoberlin.lymbo.controller.accelerometer;

import android.app.Activity;

import java.util.Observable;
import java.util.Observer;

import de.interoberlin.sugarmonkey.controller.accelerometer.AccelerationEvent;
import de.interoberlin.sugarmonkey.controller.accelerometer.Accelerometer;

public class Simulation implements Observer
{
	private static Simulation	instance;

	private static final float	MAX_VALUE	= 10.0f;
	private static final float	MIN_VALUE	= -10.0f;

	private static float		MAX_FACTOR	= 1.0f;
	private static float		MIN_FACTOR	= 1.0f;

	// Data values
	private static float		dataX		= Float.MAX_VALUE;
	private static float		dataY		= Float.MAX_VALUE;

	// Measured values + offset
	private static float		rawX		= Integer.MAX_VALUE;
	private static float		rawY		= Integer.MAX_VALUE;

	// Measured values + offset + rounded
	private static int			x			= Integer.MAX_VALUE;
	private static int			y			= Integer.MAX_VALUE;

	private Activity			activity;

	private Simulation(Activity activity)
	{
		this.activity = activity;
	}

	public static Simulation getInstance(Activity activity)
	{
		if (instance == null)
		{
			instance = new Simulation(activity);
		}

		return instance;
	}

	@Override
	public void update(Observable observable, Object data)
	{
		// Get acceleration data
		dataX = ((AccelerationEvent) data).getX();
		dataY = ((AccelerationEvent) data).getY();

		// Get sensibilities
		float sensibilityX = 1.0f;
		float sensibilityY = 1.0f;

		float offsetX = SugarMonkeyController.getOffsetX();
		float offsetY = SugarMonkeyController.getOffsetY();

		// Retrieve values from observed AccelerationEvent
		rawX = normalize(dataX - offsetX, sensibilityX);
		rawY = normalize(dataY - offsetY, sensibilityY);

		x = Math.round(rawX);
		y = Math.round(rawY);
	}

	public void start()
	{
		// BolydeActivity.uiToast("Simulation started");
		Accelerometer.getInstance(activity).start();
	}

	public void stop()
	{
		// BolydeActivity.uiToast("Simulation stopped");
		Accelerometer.getInstance(activity).stop();
		// BroadcastThread.getInstance().stop();
	}

	private float normalize(float f, float sensibility)
	{
		float FACTOR = (((MAX_FACTOR - MIN_FACTOR) / 100) * sensibility) + MIN_FACTOR;

		if (f * FACTOR > MAX_VALUE)
		{
			return MAX_VALUE;
		} else if (f * FACTOR < MIN_VALUE)
		{
			return MIN_VALUE;
		} else
		{
			return f * FACTOR;
		}
	}

	public static int getX()
	{
		return x;
	}

	public static void setX(int x)
	{
		Simulation.x = x;
	}

	public static int getY()
	{
		return y;
	}

	public static void setY(int y)
	{
		Simulation.y = y;
	}

	public static float getDataX()
	{
		return dataX;
	}

	public static float getDataY()
	{
		return dataY;
	}

	public static float getRawX()
	{
		return rawX;
	}

	public static float getRawY()
	{
		return rawY;
	}
}
