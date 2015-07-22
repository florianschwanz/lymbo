/*
 * Copyright (C) 2014 Antonio Leiva Gordillo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.interoberlin.lymbo.view.activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.App;
import de.interoberlin.lymbo.controller.accelerometer.Accelerator;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EGradleProperty;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.view.dialogfragments.GiveFeedbackDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.ReportErrorDialogFragment;

public abstract class BaseActivity extends ActionBarActivity implements Accelerator.OnShakeListener, GiveFeedbackDialogFragment.OnGiveFeedbackListener {
    // Views
    private Toolbar toolbar;

    // Accelerometer
    private SensorManager sensorManager;
    private Sensor accelerator;

    // Properties
    private static int VIBRATION_DURATION;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get instances of managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Properties
        VIBRATION_DURATION = Integer.parseInt(Configuration.getProperty(this, EProperty.VIBRATION_DURATION));
    }

    protected void onResume() {
        super.onResume();

        Accelerator.getInstance().setOnShakeListener(this);
        sensorManager.registerListener(Accelerator.getInstance(), accelerator, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(Accelerator.getInstance());

        super.onPause();
    }

    @Override
    public void onShake(int count) {
        if (!App.getInstance().isGiveFeedbackDialogActive()) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
            new GiveFeedbackDialogFragment().show(getFragmentManager(), "okay");
        }
    }

    @Override
    public void onGiveFeedbackDialogDialogComplete() {
        String appName = getResources().getString(R.string.app_name);
        String versionMajor = Configuration.getProperty(this, EGradleProperty.VERSION_MAJOR);
        String versionMinor = Configuration.getProperty(this, EGradleProperty.VERSION_MINOR);
        String versionPatch = Configuration.getProperty(this, EGradleProperty.VERSION_PATCH);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]
                {"support@interoberlin.de"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback) + " " + appName + " " + versionMajor + "." + versionMinor + "." + versionPatch);
        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_mail)));
    }

    protected void handleException(Exception e) {
        LoggingUtil.writeException(this, e);
        new ReportErrorDialogFragment().show(getFragmentManager(), "okay");
    }

    // --------------------
    // Getters / Setters
    // --------------------

    protected abstract int getLayoutResource();

    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    protected void setDisplayHomeAsUpEnabled(boolean enabled) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
    }
}
