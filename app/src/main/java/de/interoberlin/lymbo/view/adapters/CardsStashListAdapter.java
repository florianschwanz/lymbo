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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.components.AComponent;
import de.interoberlin.lymbo.core.model.v1.impl.components.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.components.Image;
import de.interoberlin.lymbo.core.model.v1.impl.components.Result;
import de.interoberlin.lymbo.core.model.v1.impl.components.Text;
import de.interoberlin.lymbo.core.model.v1.impl.components.Title;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsStashActivity;
import de.interoberlin.lymbo.view.components.ChoiceView;
import de.interoberlin.lymbo.view.components.ImageView;
import de.interoberlin.lymbo.view.components.ResultView;
import de.interoberlin.lymbo.view.components.TagView;
import de.interoberlin.lymbo.view.components.TextView;
import de.interoberlin.lymbo.view.components.TitleView;

public class CardsStashListAdapter extends ArrayAdapter<Card> {
    // <editor-fold defaultstate="expanded" desc="Members">

    // Context
    private Context context;
    private Activity activity;

    // View
    static class ViewHolder {
        FrameLayout v;
        @BindView(R.id.rlMain) RelativeLayout rlMain;
        @BindView(R.id.llTags) LinearLayout llTags;
        @BindView(R.id.ivUndo) android.widget.ImageView ivUndo;

        public ViewHolder(View v) {
            this.v = (FrameLayout) v;
            ButterKnife.bind(this, v);
        }
    }

    // Controller
    CardsController cardsController;

    // Filter
    private List<Card> filteredItems = new ArrayList<>();
    private List<Card> originalItems = new ArrayList<>();
    private CardListFilter cardListFilter;
    private final Object lock = new Object();

    // </editor-fold>

    // --------------------
    // Constructors
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Constructors">

    public CardsStashListAdapter(Context context, Activity activity, int resource, List<Card> items) {
        super(context, resource, items);
        cardsController = CardsController.getInstance();

        this.filteredItems = items;
        this.originalItems = items;

        this.context = context;
        this.activity = activity;

        filter();
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Methods">

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

        ViewHolder viewHolder;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        final ViewHolder viewHolderFinal = viewHolder;

        // Add sides
        for (Side side : card.getSides()) {
            LayoutInflater li = LayoutInflater.from(context);
            LinearLayout llSide = (LinearLayout) li.inflate(R.layout.side, parent, false);
            LinearLayout llComponents = (LinearLayout) llSide.findViewById(R.id.llComponents);

            // Add components
            for (AComponent c : side.getComponents()) {
                switch (c.getType()) {
                    case TITLE: {
                        llComponents.addView(new TitleView(context, (Title) c));
                        break;
                    }
                    case TEXT: {
                        llComponents.addView(new TextView(context, (Text) c));
                        break;
                    }
                    case IMAGE: {
                        llComponents.addView(new ImageView(context, (Image) c));
                        break;
                    }
                    case CHOICE: {
                        llComponents.addView(new ChoiceView(context, (Choice) c));
                        break;
                    }
                    case RESULT: {
                        llComponents.addView(new ResultView(context, (Result) c));
                        break;
                    }
                }
            }

            llSide.setVisibility(View.INVISIBLE);

            viewHolder.rlMain.addView(llSide);
        }

        // Display width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int displayWidth = displaymetrics.widthPixels;

        viewHolder.rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);

        // Tags
        for (Tag tag : card.getTags()) {
            if (!tag.getValue().equals(context.getResources().getString(R.string.no_tag)))
                viewHolder.llTags.addView(new TagView(context, tag));
        }

        // Reveal : undo
        viewHolder.ivUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = ViewUtil.toLeft(context, viewHolderFinal.v, displayWidth);
                viewHolderFinal.v.startAnimation(anim);

                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation anim = ViewUtil.collapse(context, viewHolderFinal.v);
                        viewHolderFinal.v.startAnimation(anim);

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
            viewHolderFinal.v.setTranslationX(displayWidth);

            Animation anim = ViewUtil.expand(context, viewHolderFinal.v);
            viewHolderFinal.v.startAnimation(anim);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    card.setRestoring(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation anim = ViewUtil.fromLeft(context, viewHolderFinal.v, displayWidth);
                    viewHolderFinal.v.startAnimation(anim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        return v;
    }

    // </editor-fold>

    // --------------------
    // Methods - Actions
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Actions">

    /**
     * Restores a card
     *
     * @param pos  position of item
     * @param card card
     */
    private void restore(int pos, Card card) {
        cardsController.restore(getContext(), card);
        ((CardsStashActivity) activity).restore(pos, card);
        filter();
    }

    // </editor-fold>

    // --------------------
    // Methods - Filter
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Filter">

    /*
    public List<Card> getFilteredItems() {
        return filteredItems;
    }
    */

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

    // </editor-fold>

    // --------------------
    // Inner classes
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Inner classes">

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

    // </editor-fold>
}