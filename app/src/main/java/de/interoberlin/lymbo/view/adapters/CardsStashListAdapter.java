package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.Image;
import de.interoberlin.lymbo.core.model.v1.impl.Result;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.Text;
import de.interoberlin.lymbo.core.model.v1.impl.Title;
import de.interoberlin.lymbo.core.model.v1.objects.ComponentObject;
import de.interoberlin.lymbo.core.model.v1.objects.SideObject;
import de.interoberlin.lymbo.core.model.v1.objects.TagObject;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsStashActivity;
import de.interoberlin.lymbo.view.components.ChoiceView;
import de.interoberlin.lymbo.view.components.ResultView;
import de.interoberlin.lymbo.view.components.TagView;
import de.interoberlin.lymbo.view.components.TextView;
import de.interoberlin.lymbo.view.components.TitleView;

public class CardsStashListAdapter extends ArrayAdapter<Card> {
    // Context
    private Context context;
    private Activity activity;

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

        this.context = context;
        this.activity = activity;

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

        // Load views
        final FrameLayout flCard = (FrameLayout) vi.inflate(R.layout.card_stash, parent, false);
        final RelativeLayout rlMain = (RelativeLayout) flCard.findViewById(R.id.rlMain);
        final LinearLayout llTags = (LinearLayout) flCard.findViewById(R.id.llTags);
        final android.widget.ImageView ivUndo = (android.widget.ImageView) flCard.findViewById(R.id.ivUndo);

        // Add sides
        for (SideObject side : card.getSide()) {
            LayoutInflater li = LayoutInflater.from(context);
            LinearLayout llSide = (LinearLayout) li.inflate(R.layout.side, parent, false);
            LinearLayout llComponents = (LinearLayout) llSide.findViewById(R.id.llComponents);

            // Add components
            for (ComponentObject c : side.getComponent()) {
                if (c instanceof Title)
                    llComponents.addView(new TitleView(context, (Title) c));
                if (c instanceof Text)
                    llComponents.addView(new TextView(context, (Text) c));
                if (c instanceof Image)
                    llComponents.addView(new de.interoberlin.lymbo.view.components.ImageView(context, (Image) c));
                if (c instanceof Choice)
                    llComponents.addView(new ChoiceView(context, (Choice) c));
                if (c instanceof Result)
                    llComponents.addView(new ResultView(context, (Result) c));
            }

            llSide.setVisibility(View.INVISIBLE);

            rlMain.addView(llSide);
        }

        // Display width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int displayWidth = displaymetrics.widthPixels;

        rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);

        // Add tags
        for (TagObject t : card.getTag()) {
            if (!t.getValue().equals(getResources().getString(R.string.no_tag))) {
                llTags.addView(new TagView(context, (Tag) t));
            }
        }

        // Reveal : undo
        ivUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = ViewUtil.toLeft(context, flCard, displayWidth);
                flCard.startAnimation(anim);

                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation anim = ViewUtil.collapse(context, flCard);
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

            Animation anim = ViewUtil.expand(context, flCard);
            flCard.startAnimation(anim);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    card.setRestoring(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation anim = ViewUtil.fromLeft(context, flCard, displayWidth);
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
     * @param pos  position of item
     * @param card card
     */
    private void restore(int pos, Card card) {
        cardsController.restore(card);
        ((CardsStashActivity) activity).restore(pos, card);
        filter();
    }

    // --------------------
    // Methods - Filter
    // --------------------

    public List<Card> getFilteredItems() {
        return filteredItems;
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
    // Methods - Util
    // --------------------

    private void vibrate(int vibrationDuration) {
        ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(vibrationDuration);
    }

    private Resources getResources() {
        return activity.getResources();
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