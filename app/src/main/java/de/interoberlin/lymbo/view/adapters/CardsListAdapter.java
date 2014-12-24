package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import de.interoberlin.lymbo.controller.ComponentsController;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.HintComponent;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.EditCardActivity;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;
import de.interoberlin.mate.lib.util.Toaster;

public class CardsListAdapter extends ArrayAdapter<Card> {
    Context c;
    Activity a;

    // Controllers
    ComponentsController componentsController = ComponentsController.getInstance();

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
    public View getView(final int position, View v, ViewGroup parent) {
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        CardView cv = (CardView) vi.inflate(R.layout.card, null);

        // Load front views
        final RelativeLayout front = (RelativeLayout) cv.findViewById(R.id.front);
        final LinearLayout llComponentsFront = (LinearLayout) cv.findViewById(R.id.llComponentsFront);
        final LinearLayout llBottomFront = (LinearLayout) cv.findViewById(R.id.llBottomFront);
        final ImageView ivFlipFront = (ImageView) cv.findViewById(R.id.ivFlipFront);
        final ImageView ivEditFront = (ImageView) cv.findViewById(R.id.ivEditFront);
        final ImageView ivHintFront = (ImageView) cv.findViewById(R.id.ivHint);

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

        // Handle hint
        if (hint != null) {
            final String hintText = hint;
            ivHintFront.setOnClickListener(new View.OnClickListener() {
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
            remove(ivHintFront);
        }

        remove(llBottomBack);

        // Default visibility : front
        front.setVisibility(View.VISIBLE);
        back.setVisibility(View.INVISIBLE);

        // Add actions

        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Clicked on front");
                front.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);

                // Handle quiz card
                if (getItem(position).getFront().containsChoice() && getItem(position).getBack().containsResult()) {
                    // Default result : CORRECT
                    getItem(position).getBack().getFirstResultComponent().setValue("CORRECT");

                    for (Answer a : getItem(position).getFront().getFirstChoiceComponent().getAnswers()) {
                        if (a.isCorrect() != a.isSelected()) {
                            // At least on answer is wrong : WRONG
                            getItem(position).getBack().getFirstResultComponent().setValue("WRONG");
                            break;
                        }
                    }

                    // Re-draw back components
                    llComponentsBack.removeAllViews();
                    if (getItem(position).getBack() != null) {
                        for (Displayable d : getItem(position).getBack().getComponents()) {
                            llComponentsBack.addView(d.getView(c, a, llComponentsBack));
                        }
                    }
                }
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

        ivEditFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                componentsController.setCard(card);
                Intent openStartingPoint = new Intent(((CardsActivity) c), EditCardActivity.class);
                ((CardsActivity) c).startActivity(openStartingPoint);
            }
        });

        return cv;
    }

    private void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }
}