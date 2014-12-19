package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.components.HintComponent;
import de.interoberlin.lymbo.model.card.components.SVGComponent;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;
import de.interoberlin.mate.lib.util.Toaster;

public class CardsListAdapter extends ArrayAdapter<Card> {
    Context c;
    Activity a;

    // --------------------
    // Constructors
    // --------------------

    public CardsListAdapter(Context context, Activity activity, int textViewResourceId) {
        super(context, textViewResourceId);

        this.c = context;
        this.a = activity;
    }

    public CardsListAdapter(Context context, Activity activity, int resource, List<Card> items) {
        super(context, resource, items);

        this.c = context;
        this.a = activity;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        CardView cv = (CardView) vi.inflate(R.layout.card, null);

        // Load front views
        final RelativeLayout front = (RelativeLayout) cv.findViewById(R.id.front);
        final LinearLayout llComponentsFront = (LinearLayout) cv.findViewById(R.id.llComponentsFront);
        final LinearLayout llBottomFront = (LinearLayout) cv.findViewById(R.id.llBottomFront);
        final ImageView ivFlipFront = (ImageView) cv.findViewById(R.id.ivFlipFront);
        final ImageView ivHint = (ImageView) cv.findViewById(R.id.ivHint);

        // Load back views
        final LinearLayout llComponentsBack = (LinearLayout) cv.findViewById(R.id.llComponentsBack);
        final RelativeLayout back = (RelativeLayout) cv.findViewById(R.id.back);
        final LinearLayout llBottomBack = (LinearLayout) cv.findViewById(R.id.llBottomBack);
        final ImageView ivFlipBack = (ImageView) cv.findViewById(R.id.ivFlipBack);

        // Add components to front
        if (getItem(position).getFront() != null) {
            for (Displayable d : getItem(position).getFront().getComponents()) {
                llComponentsFront.addView(d.getView(c, a, llComponentsFront));
            }
        }

        // Add components to back
        if (getItem(position).getBack() != null) {
            for (Displayable d : getItem(position).getBack().getComponents()) {
                llComponentsBack.addView(d.getView(c, a, llComponentsBack));
            }
        }

        llComponentsFront.setGravity(Gravity.CENTER_VERTICAL);
        llComponentsBack.setGravity(Gravity.CENTER_VERTICAL);

        final Card card = getItem(position);

        // Get (first) hint component
        String hint = null;
        for (Displayable d : card.getFront().getComponents()) {
            if (d instanceof HintComponent) {
                hint = ((HintComponent) d).getValue();
                break;
            }
        }

        if (hint != null) {
            final String hintText = hint;
            ivHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
                    Bundle b = new Bundle();
                    b.putCharSequence("type", EDialogType.HINT.toString());
                    b.putCharSequence("title", a.getResources().getString(R.string.hint));
                    b.putCharSequence("message", hintText);

                    displayDialogFragment.setArguments(b);
                    displayDialogFragment.show(a.getFragmentManager(), "okay");

                }
            });
        } else {
            remove(llBottomFront);
        }

        remove(llBottomBack);

        // Default visibility : front
        front.setVisibility(View.VISIBLE);
        back.setVisibility(View.INVISIBLE);

        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Clicked on front");
                front.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Clicked on back");
                front.setVisibility(View.VISIBLE);
                back.setVisibility(View.INVISIBLE);
            }
        });

        return cv;
    }

    public void resume() {
        for (Card card : CardsController.getCards()) {
            for (Displayable d : card.getFront().getComponents()) {
                if (d instanceof SVGComponent) {
                    ((SVGComponent) d).resume();
                }
            }
        }
    }

    public void pause() {

    }

    private void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }
}