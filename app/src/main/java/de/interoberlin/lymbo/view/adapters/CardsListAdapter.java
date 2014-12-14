package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.components.HintComponent;
import de.interoberlin.lymbo.model.card.components.SVGComponent;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;

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

        LinearLayout llComponents = (LinearLayout) cv.findViewById(R.id.llComponents);
        ImageView ivHint = (ImageView) cv.findViewById(R.id.ivHint);

        if (getItem(position).getFront() != null) {
            for (Displayable d : getItem(position).getFront().getComponents()) {
                llComponents.addView(d.getView(c, a, llComponents));
            }
        }

        final Card card = getItem(position);

        ivHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint = null;

                // Get hint component
                for (Displayable d : card.getFront().getComponents()) {
                    if (d instanceof HintComponent) {
                        hint = ((HintComponent) d).getValue();
                        break;
                    }
                }

                if (hint == null) {
                    CardsActivity.uiToast(a.getResources().getString(R.string.no_hint));
                } else {
                    DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
                    Bundle b = new Bundle();
                    b.putCharSequence("type", EDialogType.HINT.toString());
                    b.putCharSequence("title", a.getResources().getString(R.string.hint));
                    b.putCharSequence("message", hint);

                    displayDialogFragment.setArguments(b);
                    displayDialogFragment.show(a.getFragmentManager(), "okay");
                }
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

}