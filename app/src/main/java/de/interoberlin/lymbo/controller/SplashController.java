package de.interoberlin.lymbo.controller;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.EMessage;

public class SplashController {

    private List<String> messages = new ArrayList<String>();

    private static SplashController instance;

    // --------------------
    // Singleton
    // --------------------

    private SplashController() {
    }

    public static SplashController getInstance() {
        if (instance == null) {
            instance = new SplashController();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void loadMessages() {
        for (int i = 0; i < EMessage.values().length; i++) {
            this.messages.add(EMessage.values()[i].getText());
        }
    }

    // --------------------
    // Getters / Setter
    // --------------------

    public List<String> getMessages()
    {
        return this.messages;
    }

    public void setMessages(ArrayList<String> messages)
    {
        this.messages = messages;
    }
}
