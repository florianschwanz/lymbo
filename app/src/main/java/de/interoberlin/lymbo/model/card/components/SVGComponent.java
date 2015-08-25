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
import de.interoberlin.sauvignon.lib.view.SVGImagePanel;

public class SVGComponent implements Displayable {
    private String color = null;
    private SVG svg = null;

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

        llSVGComponent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (svg != null) {
            int componentWidth = llSVGComponent.getLayoutParams().width;
            float orginalSVGWidth = svg.getWidth();
            float orginalSVGHeight = svg.getHeight();
            int ratio = (int) (orginalSVGWidth / orginalSVGHeight);

            SVGImagePanel svgIp = new SVGImagePanel(c, svg, 500, 500);

            llSVGComponent.setMinimumWidth((int) svg.getWidth());
            llSVGComponent.setMinimumHeight((int) svg.getHeight());
            llSVGComponent.addView(svgIp, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
