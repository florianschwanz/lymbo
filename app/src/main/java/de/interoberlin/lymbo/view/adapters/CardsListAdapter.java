package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.ResultComponent;
import de.interoberlin.lymbo.model.card.enums.EComponent;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;

public class CardsListAdapter extends ArrayAdapter<Card> {
    Context c;
    Activity a;

    // Controllers
    CardsController cardsController = CardsController.getInstance();
    // ComponentsController componentsController = ComponentsController.getInstance();

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

        if (card != null) {

            if (card.matchesChapter(cardsController.getLymbo().getChapters()) && card.matchesTag(cardsController.getLymbo().getTags())) {
                // Layout inflater
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                final FrameLayout flCard = (FrameLayout) vi.inflate(R.layout.card, parent, false);

                // Load views : components
                final RelativeLayout rlMain = (RelativeLayout) flCard.findViewById(R.id.rlMain);

                // Load views : bottom bar
                final LinearLayout llTags = (LinearLayout) flCard.findViewById(R.id.llTags);
                final View divider = flCard.findViewById(R.id.divider);
                final LinearLayout llIconbar = (LinearLayout) flCard.findViewById(R.id.llIconbar);
                final LinearLayout llFlip = (LinearLayout) flCard.findViewById(R.id.llFlip);
                final TextView tvNumerator = (TextView) flCard.findViewById(R.id.tvNumerator);
                final TextView tvDenominator = (TextView) flCard.findViewById(R.id.tvDenominator);
                final ImageView ivEdit = (ImageView) flCard.findViewById(R.id.ivEdit);
                final ImageView ivHint = (ImageView) flCard.findViewById(R.id.ivHint);

                // Load views : reveal
                // final ImageView ivDismiss = (Button) flCard.findViewById(R.id.ivDismiss);
                final ImageView ivDiscard = (ImageView) flCard.findViewById(R.id.ivDiscard);
                final ImageView ivPutToEnd = (ImageView) flCard.findViewById(R.id.ivToEnd);

                // Add sides
                for (Side side : card.getSides()) {
                    View component = side.getView(c, a, rlMain);
                    component.setVisibility(View.INVISIBLE);
                    rlMain.addView(component);
                }

                rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);

                // Tags
                for (Tag tag : card.getTags()) {
                    if (!tag.getName().equals(c.getResources().getString(R.string.no_tag)))
                        llTags.addView(tag.getView(c, a, llTags));
                }

                // Action : flip
                if (card.isFlip() && card.getSides().size() > 1) {
                    tvNumerator.setText(String.valueOf(card.getSideVisible() + 1));
                    tvDenominator.setText(String.valueOf(card.getSides().size()));

                    llFlip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            flip(card, flCard);
                        }
                    });
                } else {
                    remove(llFlip);
                }

                // Action : edit
            /*
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
            */
                remove(ivEdit);
                //}

                // Action : hint
                if (card.getHint() != null) {
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
                if (card.getSides().size() < 2 && !card.isEdit() && card.getHint() == null) {
                    remove(divider);
                    remove(llIconbar);
                }

                // Reveal : dismiss
            /*
            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss(position);
                }
            });
            */

                // Reveal : discard
            /*
            ivDiscard.setOnC lickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    discard(position);
                }
            });
            */

                // Reveal : discard
                ivPutToEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        putToEnd(position);
                    }
                });

                return flCard;
            } else {
                Space s = new Space(c);
                s.setVisibility(View.GONE);
                return s;
            }
        } else {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            return (LinearLayout) vi.inflate(R.layout.toolbar_space, parent, false);
        }
    }

    /**
     * Puts an item from the current stack away temporarily
     *
     * @param pos position of item
     */
    /*
        private void dismiss(int pos) {
        cardsController.getLymbo().getCards().remove(pos);
        notifyDataSetChanged();
    }
    */

    /**
     * Removes an item from the current stack permanently
     *
     * @param pos position of item
     */
    private void discard(int pos) {
        cardsController.getLymbo().getCards().remove(pos);
        cardsController.save();
        notifyDataSetChanged();
    }

    /**
     * Puts an item to the end of the stack
     *
     * @param pos position of item
     */
    private void putToEnd(int pos) {
        Card card = cardsController.getLymbo().getCards().get(pos);
        card.reset();

        cardsController.getLymbo().getCards().add(card);
        cardsController.getLymbo().getCards().remove(pos);
        notifyDataSetChanged();
    }

    /**
     * Displays next side
     *
     * @param flCard frameLayout of card
     */
    private void flip(Card card, FrameLayout flCard) {
        int VIBRATION_DURATION = 40;

        ((Vibrator) a.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

        final RelativeLayout rlMain = (RelativeLayout) flCard.findViewById(R.id.rlMain);
        final TextView tvNumerator = (TextView) flCard.findViewById(R.id.tvNumerator);


        // If front contains choice component make sure that at least on answer is selected
        if (!checkAnswerSelected(card))
            return;

        // Handle components
        handleQuiz(card);

        card.setSideVisible(card.getSideVisible() + 1);
        card.setSideVisible(card.getSideVisible() % card.getSides().size());

        tvNumerator.setText(String.valueOf(card.getSideVisible() + 1));

        for (View v : getAllChildren(rlMain)) {
            v.setVisibility(View.INVISIBLE);
        }

        rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);
    }

    /**
     * Returns all direct children of a view
     *
     * @param v view to get children from
     * @return list of all child views
     */
    private List<View> getAllChildren(View v) {
        List<View> children = new ArrayList<>();

        if (!(v instanceof ViewGroup)) {
            return children;
        } else {
            ViewGroup viewGroup = (ViewGroup) v;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                children.add(viewGroup.getChildAt(i));
            }
            return children;
        }
    }

    private boolean checkAnswerSelected(Card card) {
        if (card.getSides().get(card.getSideVisible()).contains(EComponent.CHOICE)) {
            for (Answer a : ((ChoiceComponent) card.getSides().get(0).getFirst(EComponent.CHOICE)).getAnswers()) {
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
        Side current = card.getSides().get(card.getSideVisible());

        Side next = null;
        if (card.getSideVisible() + 1 < card.getSides().size())
            next = card.getSides().get(card.getSideVisible() + 1);

        // Handle quiz card
        if (current != null && current.contains(EComponent.CHOICE) && next != null && next.contains(EComponent.RESULT)) {
            // Default result : CORRECT
            ((ResultComponent) next.getFirst(EComponent.RESULT)).setValue(c.getResources().getString(R.string.correct).toUpperCase());

            for (Answer a : ((ChoiceComponent) current.getFirst(EComponent.CHOICE)).getAnswers()) {
                if (a.isCorrect() != a.isSelected()) {
                    // At least on answer is wrong : WRONG
                    ((ResultComponent) next.getFirst(EComponent.RESULT)).setValue(c.getResources().getString(R.string.correct).toUpperCase());
                    break;
                }
            }

            notifyDataSetChanged();
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