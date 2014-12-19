package de.interoberlin.lymbo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.interoberlin.lymbo.EMessage;

public class SplashController {

    private List<String> messages = new ArrayList<String>();

    private static SplashController instance;

    private static float offsetX = 0F;
    private static float offsetY = 0F;

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
        this.messages = new ArrayList<String>();
        for (int i = 0; i < EMessage.values().length; i++) {
            this.messages.add(EMessage.values()[i].getText());
        }
    }

    public String getRandomMessage() {
        return this.messages.get(new Random().nextInt(messages.size()));
    }

    // --------------------
    // Getters / Setter
    // --------------------

    public List<String> getMessages() {
        return this.messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public static float getOffsetX() {
        return offsetX;
    }

    public static void setOffsetX(float offsetX) {
        SplashController.offsetX = offsetX;
    }

    public static float getOffsetY() {
        return offsetY;
    }

    public static void setOffsetY(float offsetY) {
        SplashController.offsetY = offsetY;
    }

}
