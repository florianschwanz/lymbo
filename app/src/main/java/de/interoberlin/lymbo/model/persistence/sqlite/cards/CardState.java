package de.interoberlin.lymbo.model.persistence.sqlite.cards;


public class CardState {
    private String uuid;
    private int stashed;

    // --------------------
    // Constructors
    // --------------------

    public CardState() {

    }

    public CardState(String uuid, int stashed) {
        this.uuid = uuid;
        this.stashed = stashed;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStashed() {
        return stashed;
    }

    public void setStashed(int stashed) {
        this.stashed = stashed;
    }
}
