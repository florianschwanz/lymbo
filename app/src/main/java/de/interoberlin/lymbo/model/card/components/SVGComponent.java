package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.sauvignon.lib.model.svg.SVG;
import de.interoberlin.sauvignon.lib.model.util.SVGPaint;
import de.interoberlin.sauvignon.lib.view.SVGPanel;

public class SVGComponent implements Displayable {
    private String color = "#FFFFFF";
    private SVG svg = null;
    private SVGPanel panel;

    private boolean flip = false;

    // --------------------
    // Constructors
    // --------------------

    public SVGComponent() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llSVGComponent = (LinearLayout) li.inflate(R.layout.component_svg, parent, false);

        if (svg != null) {
            panel = new SVGPanel(c);
            panel.setSVG(svg);

            if (color.length() == 4) {
                color = "" + color.charAt(0) + color.charAt(0) + color.charAt(1) + color.charAt(1) + color.charAt(2) + color.charAt(2);
            }

            int colorA = (255);
            int colorR = Integer.parseInt(color.substring(1, 3), 16);
            int colorG = Integer.parseInt(color.substring(3, 5), 16);
            int colorB = Integer.parseInt(color.substring(5, 7), 16);

            panel.setBackgroundColor(new SVGPaint(colorA, colorR, colorG, colorB));

            llSVGComponent.setMinimumWidth((int) svg.getWidth());
            llSVGComponent.setMinimumHeight((int) svg.getHeight());
            llSVGComponent.addView(panel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // panel.display((int) svg.getWidth(), (int) svg.getHeight());
        }

        return llSVGComponent;
    }

    @Override
    public View getEditableView(Context c, final Activity a, ViewGroup parent) {
        return new View(c);
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public SVG getSVG() {
        return svg;
    }

    public void setSVG(SVG svg) {
        this.svg = svg;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}
