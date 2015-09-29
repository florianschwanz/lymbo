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
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mrengineer13.snackbar.SnackBar;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.accelerometer.Accelerator;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.LoggingUtil;
import de.interoberlin.lymbo.view.dialogfragments.GiveFeedbackDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.ReportErrorDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.ShowErrorDialogFragment;

public abstract class BaseActivity extends AppCompatActivity implements Accelerator.OnShakeListener, GiveFeedbackDialogFragment.OnCompleteListener, ReportErrorDialogFragment.OnCompleteListener, ShowErrorDialogFragment.OnCompleteListener {
    private SensorManager sensorManager;

    // Properties
    private static int VIBRATION_DURATION;

    private Sensor accelerator;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        // Load views
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get instances of managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Properties
        Resources res = getResources();
        VIBRATION_DURATION = res.getInteger(R.integer.vibration_duration);
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
            new GiveFeedbackDialogFragment().show(getFragmentManager(), GiveFeedbackDialogFragment.TAG);
        }
    }

    @Override
    public void onGiveFeedbackDialogDialogComplete() {
        Resources res = getResources();

        String appName = res.getString(R.string.app_name);
        String versionMajor = Configuration.getGradleProperty(this, res.getString(R.string.version_major));
        String versionMinor = Configuration.getGradleProperty(this, res.getString(R.string.version_minor));
        String versionPatch = Configuration.getGradleProperty(this, res.getString(R.string.version_patch));

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]
                {"support@interoberlin.de"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback) + " " + appName + " " + versionMajor + "." + versionMinor + "." + versionPatch);
        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_mail)));
    }

    @Override
    public void onSendReport(String stacktrace) {
        LoggingUtil.writeStacktraceToFile(this, stacktrace);
        LoggingUtil.sendErrorLog(this, this);
    }

    @Override
    public void onShowErrorDialogComplete(String stacktrace) {

    }

    @Override
    public void onShowStacktrace(String stacktrace) {
        ShowErrorDialogFragment dialog = new ShowErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_stacktrace), stacktrace);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), ShowErrorDialogFragment.TAG);
    }

    protected void handleException(Exception e) {
        ReportErrorDialogFragment dialog = new ReportErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_stacktrace), LoggingUtil.getStackTrace(e));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), ReportErrorDialogFragment.TAG);
    }

    /**
     * Display a snack
     *
     * @param messageClickListener activity that implements SnackBar.OnMessageClickListener
     * @param messageId            id of message to display
     */
    public void snack(SnackBar.OnMessageClickListener messageClickListener, int messageId) {
        snack(messageClickListener, messageId, SnackBar.Style.INFO);
    }

    /**
     * Display a snack
     *
     * @param messageClickListener activity that implements SnackBar.OnMessageClickListener
     * @param messageId            id of message to display
     * @param style                snack bar style
     */
    public void snack(SnackBar.OnMessageClickListener messageClickListener, int messageId, SnackBar.Style style) {
        new SnackBar.Builder(this)
                .withOnClickListener(messageClickListener)
                .withMessageId(messageId)
                .withStyle(style)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Display a snack
     *
     * @param messageClickListener activity that implements SnackBar.OnMessageClickListener
     * @param messageId            id of message to display
     * @param actionMessageId      id of action message to display
     */
    public void snack(SnackBar.OnMessageClickListener messageClickListener, int messageId, int actionMessageId) {
        new SnackBar.Builder(this)
                .withOnClickListener(messageClickListener)
                .withMessageId(messageId)
                .withActionMessageId(actionMessageId)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    // --------------------
    // Getters / Setters
    // --------------------

    protected abstract int getLayoutResource();

    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    protected void setActionBarIcon(int iconRes) {
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationIcon(iconRes);
    }

    protected void setDisplayHomeAsUpEnabled(boolean enabled) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
    }
}
