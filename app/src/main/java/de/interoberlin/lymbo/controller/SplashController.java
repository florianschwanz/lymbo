package de.interoberlin.lymbo.controller;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.EMessage;

public class SplashController {
    // Activity
    private Activity activity;

    private List<String> messages = new ArrayList<>();

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
}
