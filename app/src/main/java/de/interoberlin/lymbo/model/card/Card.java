package de.interoberlin.lymbo.model.card;

public class Card {
    private int id;
    private Side front;
    private Side back;

    private boolean flip = true;
    private boolean edit = false;

    // -------------------------
    // Constructors
    // -------------------------

    public Card() {
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Side getFront() {
        return front;
    }

    public void setFront(Side front) {
        this.front = front;
    }

    public Side getBack() {
        return back;
    }

    public void setBack(Side back) {
        this.back = back;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
}
