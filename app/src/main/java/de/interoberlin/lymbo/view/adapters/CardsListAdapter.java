package de.interoberlin.lymbo.view.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.components.AComponent;
import de.interoberlin.lymbo.core.model.v1.impl.components.Answer;
import de.interoberlin.lymbo.core.model.v1.impl.components.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.components.EComponentType;
import de.interoberlin.lymbo.core.model.v1.impl.components.Image;
import de.interoberlin.lymbo.core.model.v1.impl.components.Result;
import de.interoberlin.lymbo.core.model.v1.impl.components.Text;
import de.interoberlin.lymbo.core.model.v1.impl.components.Title;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.components.ChoiceView;
import de.interoberlin.lymbo.view.components.ResultView;
import de.interoberlin.lymbo.view.components.TagView;
import de.interoberlin.lymbo.view.components.TitleView;

public class CardsListAdapter extends ArrayAdapter<Card> implements Filterable {
    // <editor-fold defaultstate="expanded" desc="Members">

    // Context
    private Context context;
    private OnCompleteListener ocListener;

    // View
    static class ViewHolder {
        private FrameLayout v;
        @BindView(R.id.rlMain) RelativeLayout rlMain;
        @BindView(R.id.llTags) LinearLayout llTags;
        @BindView(R.id.llFlip) LinearLayout llFlip;
        @BindView(R.id.tvNumerator) TextView tvNumerator;
        @BindView(R.id.tvDenominator) TextView tvDenominator;
        @BindView(R.id.ivNote) ImageView ivNote;
        @BindView(R.id.ivFavorite) ImageView ivFavorite;
        @BindView(R.id.ivHint) ImageView ivHint;
        @BindView(R.id.llNoteBar) LinearLayout llNoteBar;
        @BindView(R.id.tvNote) TextView tvNote;

        public ViewHolder(View v) {
            this.v = (FrameLayout) v;
            ButterKnife.bind(this, v);
        }
    }

    // Controller
    private CardsController cardsController;

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

    public CardsListAdapter(Context context, OnCompleteListener ocListener, int resource, List<Card> items) {
        super(context, resource, items);
        cardsController = CardsController.getInstance();

        this.filteredItems = items;
        this.originalItems = items;

        this.context = context;
        this.ocListener = ocListener;

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
        final float minScreenWidth = context.getResources().getDimension(R.dimen.min_screen_width);

        final ViewHolder viewHolder;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        // Tint
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolder.ivNote.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
            viewHolder.ivFavorite.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
            viewHolder.ivHint.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
        }

        // Context menu
        v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 0, 0, getResources().getString(R.string.edit))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                ocListener.onClickEdit(card);
                                return false;
                            }
                        });
                contextMenu.add(0, 1, 0, getResources().getString(R.string.stash_card))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Animation a = ViewUtil.collapse(context, viewHolder.v);
                                viewHolder.v.startAnimation(a);

                                a.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        ocListener.onClickStash(position, card);
                                        filter();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                                return false;
                            }
                        });

                contextMenu.add(0, 2, 0, getResources().getString(R.string.copy_card))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                ocListener.onClickCopy(card);
                                return false;
                            }
                        });

                contextMenu.add(0, 3, 0, getResources().getString(R.string.move_card))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                ocListener.onClickMove(card);
                                return false;
                            }
                        });
            }
        });

        // Add sides
        if (!card.getSides().isEmpty()) {
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
                            llComponents.addView(new de.interoberlin.lymbo.view.components.TextView(context, (Text) c));
                            break;
                        }
                        case IMAGE: {
                            llComponents.addView(new de.interoberlin.lymbo.view.components.ImageView(context, (Image) c));
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

            // Set one side visible
            viewHolder.rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);
        } else {
            Title title = new Title(getResources().getString(R.string.card_does_not_contain_any_sides));
            Text text = new Text(getResources().getString(R.string.how_useful));

            LayoutInflater li = LayoutInflater.from(context);
            LinearLayout llSide = (LinearLayout) li.inflate(R.layout.side, parent, false);
            LinearLayout llComponents = (LinearLayout) llSide.findViewById(R.id.llComponents);

            llComponents.addView(new TitleView(context, title));
            llComponents.addView(new de.interoberlin.lymbo.view.components.TextView(context, text));

            llSide.setVisibility(View.VISIBLE);
            viewHolder.rlMain.addView(llSide);
        }

        if (!card.isNoteExpanded())
            viewHolder.llNoteBar.getLayoutParams().height = 0;
        if (card.isFavorite()) {
            viewHolder.ivFavorite.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_star_black_36dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.ivFavorite.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
            }
        } else {
            viewHolder.ivFavorite.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_star_border_black_36dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.ivFavorite.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
            }
        }

        String note = cardsController.getNote(context, card.getId());

        // Add note
        if (note != null && !note.isEmpty()) {
            viewHolder.tvNote.setText(note);
        }

        // Add tags
        Collections.sort(card.getTags(), new Comparator<Tag>() {
            @Override
            public int compare(Tag lhs, Tag rhs) {
                return lhs.getValue().compareTo(rhs.getValue());
            }
        });
        viewHolder.llTags.removeAllViews();
        for (Tag tag : card.getTags()) {
            if (!tag.getValue().equals(getResources().getString(R.string.no_tag))) {
                TagView cvTag = new TagView(context, tag);
                cvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ocListener.onClickSelectTags();
                    }
                });

                viewHolder.llTags.addView(cvTag);
            }
        }

        // Action : flip
        if (card.getSides().size() > 1) {
            viewHolder.tvNumerator.setText(String.valueOf(card.getSideVisible() + 1));
            viewHolder.tvDenominator.setText(String.valueOf(card.getSides().size()));
        } else {
            ViewUtil.remove(viewHolder.llFlip);
        }

        // Action : note
        viewHolder.ivNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleNote(card, viewHolder.llNoteBar, viewHolder.ivNote);
            }
        });

        // Action : edit note
        viewHolder.tvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocListener.onClickEditNote(card, viewHolder.tvNote.getText().toString());
            }
        });

        // Action : favorite
        viewHolder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocListener.onClickToggleFavorite(card);
            }
        });

        // Action : hint
        if (card.getHint() != null) {
            viewHolder.ivHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ocListener.onClickHint(card);
                }
            });
        } else {
            ViewUtil.remove(viewHolder.ivHint);
        }

        // Restoring animation
        if (card.isRestoring()) {
            v.setTranslationX(minScreenWidth);

            Animation anim = ViewUtil.expand(context, v);
            v.startAnimation(anim);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    card.setRestoring(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation anim = ViewUtil.fromRight(context, viewHolder.v, (int) minScreenWidth);
                    viewHolder.v.startAnimation(anim);
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
     * Flips a card to the next side
     *
     * @param card card to be flipped
     * @param view corresponding view
     */
    public void flip(final Card card, final View view) {
        if (!checkAnswerSelected(card))
            return;

        final int CARD_FLIP_TIME = getResources().getInteger(R.integer.card_flip_time);
        // final int VIBRATION_DURATION_FLIP = getResources().getInteger(R.integer.vibration_duration_flip);

        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 90.0f);
        animation.setDuration(CARD_FLIP_TIME / 2);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeSide(card, view);

                ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationY", -90.0f, 0.0f);
                animation.setDuration(CARD_FLIP_TIME / 2);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();
            }
        }, CARD_FLIP_TIME / 2);
    }

    /**
     * Collapses / expands the note bar
     *
     * @param card      card
     * @param llNoteBar note bar layout
     * @param ivNote    button
     */
    private void toggleNote(final Card card, final LinearLayout llNoteBar, final ImageView ivNote) {
        if (card.isNoteExpanded()) {
            Animation anim = ViewUtil.collapse(context, llNoteBar);
            llNoteBar.startAnimation(anim);
            card.setNoteExpanded(false);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivNote.setImageResource(R.drawable.ic_expand_more_black_36dp);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            Animation anim = ViewUtil.expand(context, llNoteBar);
            llNoteBar.startAnimation(anim);
            card.setNoteExpanded(true);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivNote.setImageResource(R.drawable.ic_expand_less_black_36dp);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    /**
     * Displays next side
     *
     * @param card card
     * @param view view of card
     */
    private void changeSide(Card card, View view) {
        final RelativeLayout rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
        final TextView tvNumerator = (TextView) view.findViewById(R.id.tvNumerator);

        card.updateResult();
        notifyDataSetChanged();

        card.setSideVisible((card.getSideVisible() + 1) % card.getSides().size());
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

    /**
     * Determines whether at least one answer is selected
     *
     * @param card card
     * @return whether at least one answer is selected
     */
    private boolean checkAnswerSelected(Card card) {
        if (card.getSides().get(card.getSideVisible()).contains(EComponentType.CHOICE)) {
            for (Answer a : ((Choice) card.getSides().get(0).getFirst(EComponentType.CHOICE)).getAnswers()) {
                if (a.isSelected()) {
                    return true;
                }
            }

            ocListener.onAlertNoAnswer();
            return false;
        } else {
            return true;
        }
    }

    // </editor-fold>

    // --------------------
    // Methods - Filter
    // --------------------

    // </editor-fold>

    // --------------------
    // Methods - Callbacks
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Callbacks">

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
        return cardsController.isVisible(getContext(), card);
    }

    // </editor-fold>

    // --------------------
    // Methods - Util
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Util">

    private Resources getResources() {
        return getContext().getResources();
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
            originalItems = cardsController.getCards();

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

    // --------------------
    // Callback interfaces
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Callback interfaces">

    public interface OnCompleteListener {
        void onClickEdit(Card card);

        void onClickEditNote(Card card, String text);

        void onClickStash(int position, Card card);

        void onClickSelectTags();

        void onClickHint(Card card);

        void onClickToggleFavorite(Card card);

        void onClickCopy(Card card);

        void onClickMove(Card card);

        void onAlertNoAnswer();
    }

    // </editor-fold>
}