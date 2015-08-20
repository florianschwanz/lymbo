package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.sauvignon.lib.controller.renderer.SvgRenderer;
import de.interoberlin.sauvignon.lib.model.svg.SVG;

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

        if (svg != null) {
            Bitmap bmp = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);

            // Clear canvas
            if (color != null) {
                canvas.drawColor(c.getResources().getColor(R.color.white));
            } else {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }

            // Render SVG
            canvas = SvgRenderer.renderToCanvas(canvas, svg);

            ImageView iv = new ImageView(c);
            iv.setImageBitmap(bmp);

            llSVGComponent.setMinimumWidth((int) svg.getWidth());
            llSVGComponent.setMinimumHeight((int) svg.getHeight());
            llSVGComponent.addView(iv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
