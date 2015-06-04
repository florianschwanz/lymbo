package de.interoberlin.lymbo.model.card;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.ImageComponent;
import de.interoberlin.lymbo.model.card.components.ResultComponent;
import de.interoberlin.lymbo.model.card.components.SVGComponent;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.EComponent;

public class Side implements Displayable {
    private String color = "#FFFFFF";
    private List<Displayable> components = new ArrayList<>();

    private boolean flip = false;

    // -------------------------
    // Constructors
    // -------------------------

    public Side() {
    }

    public Side(Displayable... components) {
        for (Displayable c : components) {
            this.components.add(c);
        }
    }

    // -------------------------
    // Methods
    // -------------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llSide = (LinearLayout) li.inflate(R.layout.side, parent, false);
        LinearLayout llComponents = (LinearLayout) llSide.findViewById(R.id.llComponents);


        for (Displayable d : getComponents()) {
            View component = d.getView(c, a, llComponents);
            llComponents.addView(component);

            if ((d instanceof TitleComponent && ((TitleComponent) d).isFlip()) ||
                    (d instanceof TextComponent && ((TextComponent) d).isFlip()) ||
                    (d instanceof ImageComponent && ((ImageComponent) d).isFlip()) ||
                    (d instanceof ResultComponent && ((ResultComponent) d).isFlip()) ||
                    (d instanceof SVGComponent && ((SVGComponent) d).isFlip())
                    ) {
                component.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // flipToBack(card, llFront, llBack, tvNumerator, llFront);
                    }
                });

            }
        }

        return llSide;
    }

    @Override
    public View getEditableView(Context c, final Activity a, ViewGroup parent) {
        // TODO : implement
        return null;
    }

    public boolean contains(EComponent component) {
        for (Displayable d : getComponents()) {
            if ((component == EComponent.TITLE && d instanceof TitleComponent) ||
                    (component == EComponent.TEXT && d instanceof TextComponent) ||
                    (component == EComponent.IMAGE && d instanceof ImageComponent) ||
                    (component == EComponent.CHOICE && d instanceof ChoiceComponent) ||
                    (component == EComponent.RESULT && d instanceof ResultComponent) ||
                    (component == EComponent.SVG && d instanceof SVGComponent)) {
                return true;
            }
        }

        return false;
    }

    public Displayable getFirst(EComponent component) {
        for (Displayable d : getComponents()) {
            if ((component == EComponent.TITLE && d instanceof TitleComponent) ||
                    (component == EComponent.TEXT && d instanceof TextComponent) ||
                    (component == EComponent.IMAGE && d instanceof ImageComponent) ||
                    (component == EComponent.CHOICE && d instanceof ChoiceComponent) ||
                    (component == EComponent.RESULT && d instanceof ResultComponent) ||
                    (component == EComponent.SVG && d instanceof SVGComponent)) {
                return d;
            }
        }

        return null;
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Displayable> getComponents() {
        return components;
    }

    public void setComponents(List<Displayable> components) {
        this.components = components;
    }

    public void addComponent(Displayable displayable) {
        if (components == null) {
            components = new ArrayList<>();
        }

        components.add(displayable);
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}