package de.interoberlin.lymbo.model;

public enum EMessage {
    SEARCHING_FILES("Searching files"),
    ADDING_COOKIE_FLAVOR("Adding cookie flavor");

    private String s;

    EMessage(String s) {
        this.s = s;
    }

    public String getText() {
        return s;
    }
}
