package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsStashActivity;

public class CardsStashListAdapter extends ArrayAdapter<Card> {
    // Context
    private Context c;
    private Activity a;

    // Controllers
    CardsController cardsController;

    // Filter
    private List<Card> filteredItems = new ArrayList<>();
    private List<Card> originalItems = new ArrayList<>();
    private CardListFilter cardListFilter;
    private final Object lock = new Object();

    // --------------------
    // Constructors
    // --------------------

    public CardsStashListAdapter(Context context, Activity activity, int resource, List<Card> items) {
        super(context, resource, items);
        cardsController = CardsController.getInstance(activity);

        this.filteredItems = items;
        this.originalItems = items;

        this.c = context;
        this.a = activity;

        filter();
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public int getCount() {
        return filteredItems != null ? filteredItems.size() : 0;
    }

    @Override
    public Card getItem(int position) {
        return filteredItems.get(position);
    }


    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final Card card = getItem(position);

        return getCardView(position, card, parent);
    }

    private View getCardView(final int position, final Card card, final ViewGroup parent) {
        // Layout inflater
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        final FrameLayout flCard = (FrameLayout) vi.inflate(R.layout.card_stash, parent, false);

        // Load views : components
        final RelativeLayout rlMain = (RelativeLayout) flCard.findViewById(R.id.rlMain);

        // Load views : bottom bar
        final LinearLayout llTags = (LinearLayout) flCard.findViewById(R.id.llTags);
        final ImageView ivUndo = (ImageView) flCard.findViewById(R.id.ivUndo);

        // Add sides
        for (Side side : card.getSides()) {
            LayoutInflater li = LayoutInflater.from(c);
            LinearLayout llSide = (LinearLayout) li.inflate(R.layout.side, parent, false);
            LinearLayout llComponents = (LinearLayout) llSide.findViewById(R.id.llComponents);

            // Add components
            for (Displayable d : side.getComponents()) {
                View component = d.getView(c, a, llComponents);
                llComponents.addView(component);
            }

            llSide.setVisibility(View.INVISIBLE);

            rlMain.addView(llSide);
        }

        // Display width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int displayWidth = displaymetrics.widthPixels;

        rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);

        // Tags
        for (Tag tag : card.getTags()) {
            if (!tag.getName().equals(c.getResources().getString(R.string.no_tag)))
                llTags.addView(tag.getView(c, a, llTags));
        }

        // Reveal : undo
        ivUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = ViewUtil.toLeft(c, flCard, displayWidth);
                flCard.startAnimation(anim);

                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation anim = ViewUtil.collapse(c, flCard);
                        flCard.startAnimation(anim);

                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                restore(position, card);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        if (card.isRestoring()) {
            flCard.setTranslationX(displayWidth);

            Animation anim = ViewUtil.expand(c, flCard);
            flCard.startAnimation(anim);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    card.setRestoring(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation anim = ViewUtil.fromLeft(c, flCard, displayWidth);
                    flCard.startAnimation(anim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        return flCard;
    }

    /**
     * Restores a card
     *
     * @param pos    position of item
     * @param card   card
     */
    private void restore(int pos, Card card) {
        cardsController.restore(card);
        ((CardsStashActivity) a).restore(pos, card);
        filter();
    }

    public void filter() {
        getFilter().filter("");
    }

    @Override
    public Filter getFilter() {
        if (cardListFilter == null) {
            cardListFilter = new CardListFilter();
        }
        return cardListFilter;
    }

    /**
     * Determines if a card shall be displayed
     *
     * @param card card
     * @return true if item is visible
     */
    protected boolean filterCard(Card card) {
        return card != null;
    }

    // --------------------
    // Inner classes
    // --------------------

    public class CardListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            // Copy items
            originalItems = cardsController.getCardsStashed();

            ArrayList<Card> values;
            synchronized (lock) {
                values = new ArrayList<>(originalItems);
            }

            final int count = values.size();
            final ArrayList<Card> newValues = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                final Card value = values.get(i);
                if (filterCard(value)) {
                    newValues.add(value);
                }
            }

            results.values = newValues;
            results.count = newValues.size();

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (List<Card>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}