package de.interoberlin.lymbo.controller;


import de.interoberlin.lymbo.model.card.XmlCard;

public class StackController {
    private XmlCard currentCard;
    private int currentCardId;
    private int displayHeight;
    private int displayWidth;
    private String hintMessage;

    private static StackController instance;

    // --------------------
    // Singleton
    // --------------------

    private StackController() {
    }

    public static StackController getInstance() {
        if (instance == null) {
            instance = new StackController();
        }

        return instance;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public XmlCard getCurrentCard() {
        return this.currentCard;
    }

    public void setCurrentCard(XmlCard currentCard) {
        this.currentCard = currentCard;
    }

    public int getCurrentCardId() {
        return currentCardId;
    }

    public void setCurrentCardId(int currentCardId) {
        this.currentCardId = currentCardId;
    }

    public int getCardHeightCard() {
        return (int) (getDisplayHeight() * 0.8);
    }

    public int getDisplayHeight() {
        return this.displayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    public int getDisplayWidth() {
        return this.displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public CharSequence getHintMessage() {
        return this.hintMessage;
    }

    public void setHintMessage(String hintMessage) {
        this.hintMessage = hintMessage;
    }

}
