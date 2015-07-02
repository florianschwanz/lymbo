package de.interoberlin.lymbo.controller;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.EMessage;

public class SplashController {
    // Activity
    private Activity activity;

    private List<String> messages = new ArrayList<>();

    private static float offsetX = 0F;
    private static float offsetY = 0F;

    private static SplashController instance;

    // --------------------
    // Singleton
    // --------------------

    private SplashController(Activity activity) {
        this.activity = activity;
    }

    public static SplashController getInstance(Activity activity) {
        if (instance == null) {
            instance = new SplashController(activity);
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void loadMessages() {
        this.messages = new ArrayList<>();
        for (int i = 0; i < EMessage.values().length; i++) {
            this.messages.add(EMessage.values()[i].getText());
        }
    }

    // --------------------
    // Getters / Setter
    // --------------------

    public List<String> getMessages() {
        return this.messages;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        SplashController.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        SplashController.offsetY = offsetY;
    }

}
