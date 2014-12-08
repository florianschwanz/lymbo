package de.interoberlin.lymbo.model.card;

public class Card {
    private int id;
    private Side front;
    private Side back;

    // -------------------------
    // Constructors
    // -------------------------

    public Card() {

    }

    public Card(int id, Side front) {
        this.id = id;
        this.front = front;
    }

    public Card(int id, Side front, Side back) {
        this.id = id;
        this.front = front;
        this.back = back;
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
}
