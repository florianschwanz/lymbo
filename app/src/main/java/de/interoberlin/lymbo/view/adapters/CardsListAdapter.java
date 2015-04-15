package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.ComponentsController;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.ImageComponent;
import de.interoberlin.lymbo.model.card.components.ResultComponent;
import de.interoberlin.lymbo.model.card.components.SVGComponent;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.EComponent;
import de.interoberlin.lymbo.view.activities.EditCardActivity;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;

public class CardsListAdapter extends ArrayAdapter<Card> {
    Context c;
    Activity a;

    // Controllers
    CardsController cardsController = CardsController.getInstance();
    ComponentsController componentsController = ComponentsController.getInstance();

    private int VIBRATION_DURATION = 50;
    private boolean frontVisible = true;

    // --------------------
    // Constructors
    // --------------------

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
        final Card card = getItem(position);

        if (!card.isDiscarded() && card.matchesChapter(cardsController.getLymbo().getChapters()) && card.matchesTag(cardsController.getLymbo().getTags())) {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            LinearLayout ll = (LinearLayout) vi.inflate(R.layout.card, parent);

            // Load views : front
            final LinearLayout front = (LinearLayout) ll.findViewById(R.id.front);
            final LinearLayout back = (LinearLayout) ll.findViewById(R.id.back);
            final LinearLayout llComponentsFront = (LinearLayout) ll.findViewById(R.id.llComponentsFront);
            final LinearLayout llComponentsBack = (LinearLayout) ll.findViewById(R.id.llComponentsBack);

            // Load views : bottom bar
            final LinearLayout llBottom = (LinearLayout) ll.findViewById(R.id.llBottom);
            final ImageView ivFlip = (ImageView) ll.findViewById(R.id.ivFlip);
            final ImageView ivEdit = (ImageView) ll.findViewById(R.id.ivEdit);
            final ImageView ivHint = (ImageView) ll.findViewById(R.id.ivHint);

            // Add components : front
            if (card.getFront() != null) {
                for (Displayable d : card.getFront().getComponents()) {
                    View component = d.getView(c, a, llComponentsFront);
                    llComponentsFront.addView(component);

                    if ((d instanceof TitleComponent && ((TitleComponent) d).isFlip()) ||
                            (d instanceof TextComponent && ((TextComponent) d).isFlip()) ||
                            (d instanceof ImageComponent && ((ImageComponent) d).isFlip()) ||
                            (d instanceof ResultComponent && ((ResultComponent) d).isFlip()) ||
                            (d instanceof SVGComponent && ((SVGComponent) d).isFlip())
                            ) {
                        component.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                flipToBack(card, front, back, llComponentsFront);
                            }
                        });

                    }
                }
            }

            // Add components : back
            if (card.getBack() != null) {
                for (Displayable d : card.getBack().getComponents()) {
                    View component = d.getView(c, a, llComponentsBack);
                    llComponentsBack.addView(component);

                    if ((d instanceof TitleComponent && ((TitleComponent) d).isFlip()) ||
                            (d instanceof TextComponent && ((TextComponent) d).isFlip()) ||
                            (d instanceof ImageComponent && ((ImageComponent) d).isFlip()) ||
                            (d instanceof ResultComponent && ((ResultComponent) d).isFlip()) ||
                            (d instanceof SVGComponent && ((SVGComponent) d).isFlip())
                            ) {
                        component.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                flipToFront(card, front, back, llComponentsFront);
                            }
                        });

                    }
                }
            }

            front.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);

            // Center vertically
            llComponentsFront.setGravity(Gravity.CENTER_VERTICAL);
            llComponentsBack.setGravity(Gravity.CENTER_VERTICAL);

            // Action : flip
            if (card.isFlip()) {
                ivFlip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (frontVisible) {
                            flipToBack(card, front, back, llComponentsBack);
                        } else {
                            flipToFront(card, front, back, llComponentsFront);
                        }
                    }
                });
            } else {
                remove(ivFlip);
            }

            // Action : edit
            if (card.isEdit()) {
                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        componentsController.setCard(card);
                        Intent openStartingPoint = new Intent(c, EditCardActivity.class);
                        c.startActivity(openStartingPoint);
                    }
                });
            } else {
                remove(ivEdit);
            }

            // Action : hint
            if (card.getHint() != null && frontVisible) {
                ivHint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
                        Bundle b = new Bundle();
                        b.putCharSequence("type", EDialogType.HINT.toString());
                        b.putCharSequence("title", a.getResources().getString(R.string.hint));
                        b.putCharSequence("message", card.getHint());

                        displayDialogFragment.setArguments(b);
                        displayDialogFragment.show(a.getFragmentManager(), "okay");
                    }
                });
            } else {
                remove(ivHint);
            }

            // Remove bottom bar if unnecessary
            if (!card.isFlip() && !card.isEdit() && card.getHint() == null) {
                remove(llBottom);
            }

            return ll;
        } else {
            Space s = new Space(c);
            s.setVisibility(View.GONE);
            return s;
        }
    }

    private void flipToBack(final Card card, final LinearLayout front, final LinearLayout back, final LinearLayout visible) {
        ((Vibrator) a.getSystemService(c.VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

        // If front contains choice component make sure that at least on answer is selected
        if (!checkAnswerSelected(card))
            return;

        // Switch visibility
        front.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);

        // Handle components
        handleQuiz(card);

        // Re-draw components
        visible.removeAllViews();
        if (card.getBack() != null) {
            for (Displayable d : card.getBack().getComponents()) {
                View component = d.getView(c, a, visible);
                visible.addView(component);

                if ((d instanceof TitleComponent && ((TitleComponent) d).isFlip()) ||
                        (d instanceof TextComponent && ((TextComponent) d).isFlip()) ||
                        (d instanceof ImageComponent && ((ImageComponent) d).isFlip()) ||
                        (d instanceof ResultComponent && ((ResultComponent) d).isFlip()) ||
                        (d instanceof SVGComponent && ((SVGComponent) d).isFlip())
                        ) {
                    component.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            flipToFront(card, front, back, visible);
                        }
                    });

                }
            }
        }

        frontVisible = false;
    }

    private void flipToFront(final Card card, final LinearLayout front, final LinearLayout back, final LinearLayout visible) {
        ((Vibrator) a.getSystemService(c.VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

        // Switch visibility
        front.setVisibility(View.VISIBLE);
        back.setVisibility(View.INVISIBLE);

        // Re-draw components
        visible.removeAllViews();
        if (card.getFront() != null) {
            for (Displayable d : card.getFront().getComponents()) {
                View component = d.getView(c, a, visible);
                visible.addView(component);

                if ((d instanceof TitleComponent && ((TitleComponent) d).isFlip()) ||
                        (d instanceof TextComponent && ((TextComponent) d).isFlip()) ||
                        (d instanceof ImageComponent && ((ImageComponent) d).isFlip()) ||
                        (d instanceof ResultComponent && ((ResultComponent) d).isFlip()) ||
                        (d instanceof SVGComponent && ((SVGComponent) d).isFlip())
                        ) {
                    component.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            flipToBack(card, front, back, visible);
                        }
                    });
                }
            }
        }

        frontVisible = true;
    }

    private boolean checkAnswerSelected(Card card) {
        if (card.getFront().contains(EComponent.CHOICE)) {
            for (Answer a : ((ChoiceComponent) card.getFront().getFirst(EComponent.CHOICE)).getAnswers()) {
                if (a.isSelected()) {
                    return true;
                }
            }

            DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
            Bundle b = new Bundle();
            b.putCharSequence("type", EDialogType.WARNING.toString());
            b.putCharSequence("title", a.getResources().getString(R.string.select_answer));
            b.putCharSequence("message", "");

            displayDialogFragment.setArguments(b);
            displayDialogFragment.show(a.getFragmentManager(), "okay");

            return false;
        } else {
            return true;
        }
    }

    private void handleQuiz(Card card) {
        // Handle quiz card
        if (card.getFront().contains(EComponent.CHOICE) && card.getBack().contains(EComponent.RESULT)) {
            // Default result : CORRECT
            ((ResultComponent) card.getBack().getFirst(EComponent.RESULT)).setValue("CORRECT");

            for (Answer a : ((ChoiceComponent) card.getFront().getFirst(EComponent.CHOICE)).getAnswers()) {
                if (a.isCorrect() != a.isSelected()) {
                    // At least on answer is wrong : WRONG
                    ((ResultComponent) card.getBack().getFirst(EComponent.RESULT)).setValue("WRONG");
                    break;
                }
            }
        }
    }

    /**
     * Removes a view from ViewManager
     *
     * @param v View to be removed
     */
    private void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }
}