package de.interoberlin.lymbo.controller;


import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;

public class ComponentsController {
    private static Card card;
    private static List<Displayable> componentsFront = new ArrayList<Displayable>();
    private static List<Displayable> componentsBack = new ArrayList<Displayable>();

    private static ComponentsController instance;

    // --------------------
    // Constructors
    // --------------------

    private ComponentsController() {
        init();
    }

    public static ComponentsController getInstance() {
        if (instance == null) {
            instance = new ComponentsController();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void init() {
        getComponentsFromCard();
    }

    public void getComponentsFromCard() {
        if (card != null) {
            componentsFront = card.getFront().getComponents();
            componentsFront = card.getBack().getComponents();
        }
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public static Card getCard() {
        return card;
    }

    public static void setCard(Card card) {
        ComponentsController.card = card;
    }

    public static List<Displayable> getComponentsFront() {
        return componentsFront;
    }

    public static void setComponentsFront(List<Displayable> componentsFront) {
        ComponentsController.componentsFront = componentsFront;
    }

    public static List<Displayable> getComponentsBack() {
        return componentsBack;
    }

    public static void setComponentsBack(List<Displayable> componentsBack) {
        ComponentsController.componentsBack = componentsBack;
    }
}
