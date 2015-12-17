package de.interoberlin.lymbo.view.adapters;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mrengineer13.snackbar.SnackBar;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.core.model.v1.impl.Answer;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.Image;
import de.interoberlin.lymbo.core.model.v1.impl.Result;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.Text;
import de.interoberlin.lymbo.core.model.v1.impl.Title;
import de.interoberlin.lymbo.core.model.v1.objects.AnswerObject;
import de.interoberlin.lymbo.core.model.v1.objects.CardObject;
import de.interoberlin.lymbo.core.model.v1.objects.ComponentObject;
import de.interoberlin.lymbo.core.model.v1.objects.ComponentType;
import de.interoberlin.lymbo.core.model.v1.objects.SideObject;
import de.interoberlin.lymbo.core.model.v1.objects.TagObject;
import de.interoberlin.lymbo.util.TagUtil;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.components.ChoiceView;
import de.interoberlin.lymbo.view.components.ImageView;
import de.interoberlin.lymbo.view.components.ResultView;
import de.interoberlin.lymbo.view.components.TagView;
import de.interoberlin.lymbo.view.components.TextView;
import de.interoberlin.lymbo.view.components.TitleView;
import de.interoberlin.lymbo.view.dialogfragments.CardDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.CopyCardDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.DisplayHintDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EditNoteDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.FilterCardsDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.MoveCardDialogFragment;

public class CardsListAdapter extends ArrayAdapter<Card> implements Filterable {
    // Context
    private Context context;
    private Activity activity;

    // Controllers
    private CardsController cardsController;

    // Filter
    private List<Card> filteredItems = new ArrayList<>();
    private List<Card> originalItems = new ArrayList<>();
    private CardListFilter cardListFilter;
    private final Object lock = new Object();

    // Properties
    private static int VIBRATION_DURATION;

    // --------------------
    // Constructors
    // --------------------

    public CardsListAdapter(Context context, Activity activity, int resource, List<Card> items) {
        super(context, resource, items);
        cardsController = CardsController.getInstance(activity);

        this.filteredItems = items;
        this.originalItems = items;

        this.context = context;
        this.activity = activity;

        // Properties
        VIBRATION_DURATION = getResources().getInteger(R.integer.vibration_duration);

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
        final FrameLayout flCard = (FrameLayout) vi.inflate(R.layout.card, parent, false);
        final RelativeLayout rlMain = (RelativeLayout) flCard.findViewById(R.id.rlMain);
        final LinearLayout llTags = (LinearLayout) flCard.findViewById(R.id.llTags);
        final LinearLayout llFlip = (LinearLayout) flCard.findViewById(R.id.llFlip);
        final android.widget.TextView tvNumerator = (android.widget.TextView) flCard.findViewById(R.id.tvNumerator);
        final android.widget.TextView tvDenominator = (android.widget.TextView) flCard.findViewById(R.id.tvDenominator);
        final android.widget.ImageView ivNote = (android.widget.ImageView) flCard.findViewById(R.id.ivNote);
        final android.widget.ImageView ivFavorite = (android.widget.ImageView) flCard.findViewById(R.id.ivFavorite);
        final android.widget.ImageView ivHint = (android.widget.ImageView) flCard.findViewById(R.id.ivHint);
        final LinearLayout llNoteBar = (LinearLayout) flCard.findViewById(R.id.llNoteBar);
        final android.widget.TextView tvNote = (android.widget.TextView) flCard.findViewById(R.id.tvNote);

        // Context menu
        flCard.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 0, 0, getResources().getString(R.string.edit))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                edit(card);
                                return false;
                            }
                        });
                contextMenu.add(0, 1, 0, getResources().getString(R.string.stash_card))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                stash(position, card, flCard);
                                return false;
                            }
                        });

                contextMenu.add(0, 2, 0, getResources().getString(R.string.copy_card))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                copy(card);
                                return false;
                            }
                        });

                contextMenu.add(0, 3, 0, getResources().getString(R.string.move_card))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                move(card);
                                return false;
                            }
                        });
            }
        });

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
                    llComponents.addView(new ImageView(context, (Image) c));
                if (c instanceof Choice)
                    llComponents.addView(new ChoiceView(context, (Choice) c));
                if (c instanceof Result)
                    llComponents.addView(new ResultView(context, (Result) c));
            }

            llSide.setVisibility(View.INVISIBLE);
            rlMain.addView(llSide);
        }

        // Get display width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int displayWidth = displaymetrics.widthPixels;

        // Set one side visible
        rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);

        if (!card.isNoteExpanded())
            llNoteBar.getLayoutParams().height = 0;
        if (card.isFavorite())
            ivFavorite.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_action_important));
        else
            ivFavorite.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_action_not_important));

        String note = cardsController.getNote(context, card.getId());

        // Add note
        if (note != null && !note.isEmpty()) {
            tvNote.setText(note);
        }

        // Add tags
        for (TagObject t : card.getTag()) {
            if (!t.getValue().equals(getResources().getString(R.string.no_tag))) {
                TagView tvTag = new TagView(context, (Tag) t);
                tvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTags();
                    }
                });

                llTags.addView(tvTag);
            }
        }

        // Action : flip
        if (card.getSide().size() > 1) {
            tvNumerator.setText(String.valueOf(card.getSideVisible() + 1));
            tvDenominator.setText(String.valueOf(card.getSide().size()));
        } else {
            ViewUtil.remove(llFlip);
        }

        // Action : note
        ivNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleNote(card, llNoteBar, ivNote);
            }
        });

        // Action : edit note
        tvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNote(card, tvNote.getText().toString());
            }
        });

        // Action : favorite
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(card, !card.isFavorite());
            }
        });


        // Action : hint
        if (card.getHint() != null) {
            ivHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DisplayHintDialogFragment displayHintDialogFragment = new DisplayHintDialogFragment();
                    Bundle b = new Bundle();
                    b.putCharSequence(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.hint));
                    b.putCharSequence(getResources().getString(R.string.bundle_message), card.getHint());
                    displayHintDialogFragment.setArguments(b);
                    displayHintDialogFragment.show(activity.getFragmentManager(), DisplayHintDialogFragment.TAG);
                }
            });
        } else {
            ViewUtil.remove(ivHint);
        }

        // Restoring animation
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
                    Animation anim = ViewUtil.fromRight(context, flCard, displayWidth);
                    flCard.startAnimation(anim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        return flCard;
    }

    // --------------------
    // Methods - Actions
    // --------------------

    /**
     * Opens dialog to edit card
     *
     * @param card card to be edited
     */
    private void edit(final Card card) {
        String uuid = card.getId();
        String frontTitle = ((Title) ((Side) card.getSide().get(0)).getFirst(ComponentType.TITLE)).getValue();
        String backTitle = ((Title) ((Side) card.getSide().get(1)).getFirst(ComponentType.TITLE)).getValue();
        ArrayList<String> frontTexts = new ArrayList<>();
        ArrayList<String> backTexts = new ArrayList<>();
        ArrayList<String> tagsAll = TagUtil.getDistinctValues(cardsController.getTagsAll());
        ArrayList<String> tagsSelected = TagUtil.getDistinctValues(TagUtil.getTagList(card.getTag()));
        ArrayList<String> answersValue = new ArrayList<>();
        ArrayList<Integer> answersCorrect = new ArrayList<>();
        ArrayList<String> templates = new ArrayList<>();

        for (ComponentObject c : card.getSide().get(0).getComponent()) {
            if (c instanceof Text) {
                frontTexts.add(((Text) c).getValue());
            } else if (c instanceof Choice) {
                for (AnswerObject a : ((Choice) c).getAnswer()) {
                    answersValue.add(a.getValue());
                    answersCorrect.add(a.isCorrect() ? 1 : 0);
                }
            }
        }

        for (ComponentObject c : card.getSide().get(1).getComponent()) {
            if (c instanceof Text) {
                backTexts.add(((Text) c).getValue());
            }
        }

        for (CardObject template : cardsController.getStack().getTemplate()) {
            if (template != null && template.getId() != null) {
                templates.add(template.getId());
            }
        }

        vibrate(VIBRATION_DURATION);

        CardDialogFragment dialog = new CardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.edit_card));
        bundle.putString(getResources().getString(R.string.bundle_card_id), uuid);
        bundle.putString(getResources().getString(R.string.bundle_front_title), frontTitle);
        bundle.putString(getResources().getString(R.string.bundle_back_title), backTitle);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_texts_front), frontTexts);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_texts_back), backTexts);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_templates), templates);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_answers_value), answersValue);
        bundle.putIntegerArrayList(getResources().getString(R.string.bundle_answers_correct), answersCorrect);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), CardDialogFragment.TAG);
    }

    /**
     * Stashes a card
     *
     * @param position position of item
     * @param card     card to be stashed
     * @param flCard   corresponding view
     */
    private void stash(final int position, final Card card, final FrameLayout flCard) {
        Animation a = ViewUtil.collapse(context, flCard);
        flCard.startAnimation(a);

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardsController.stash(card);

                if (activity instanceof CardsActivity)
                    ((CardsActivity) activity).stash(position, card);
                filter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * Opens dialog to copy card
     *
     * @param card card to be copied
     */
    private void copy(final Card card) {
        String uuid = card.getId();

        CopyCardDialogFragment dialog = new CopyCardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_lymbo_id), cardsController.getStack().getId());
        bundle.putString(getResources().getString(R.string.bundle_card_id), uuid);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), CopyCardDialogFragment.TAG);
    }

    /**
     * Opens dialog to move card
     *
     * @param card card to be moved
     */
    private void move(final Card card) {
        String uuid = card.getId();

        MoveCardDialogFragment dialog = new MoveCardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_lymbo_id), cardsController.getStack().getId());
        bundle.putString(getResources().getString(R.string.bundle_card_id), uuid);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), MoveCardDialogFragment.TAG);
    }

    /**
     * Opens a dialog to select tags
     */
    private void selectTags() {
        ((Vibrator) activity.getSystemService(Activity.VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

        ArrayList<String> tagsAll = TagUtil.getDistinctValues(cardsController.getTagsAll());
        ArrayList<String> tagsSelected = TagUtil.getDistinctValues(cardsController.getTagsSelected());
        Boolean displayOnlyFavorites = cardsController.isDisplayOnlyFavorites();

        FilterCardsDialogFragment dialog = new FilterCardsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        bundle.putBoolean(getResources().getString(R.string.bundle_display_only_favorites), displayOnlyFavorites);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), FilterCardsDialogFragment.TAG);
    }

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
        final int VIBRATION_DURATION_FLIP = getResources().getInteger(R.integer.vibration_duration_flip);

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

        vibrate(VIBRATION_DURATION_FLIP);
    }

    /**
     * Collapses / expands the note bar
     *
     * @param card      card
     * @param llNoteBar note bar layout
     * @param ivNote    button
     */
    private void toggleNote(final Card card, final LinearLayout llNoteBar, final android.widget.ImageView ivNote) {
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
                    ivNote.setImageResource(R.drawable.ic_action_expand);
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
                    ivNote.setImageResource(R.drawable.ic_action_collapse);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    /**
     * Opens a dialog to edit a note
     *
     * @param card card
     * @param text note text
     */
    public void editNote(Card card, String text) {
        EditNoteDialogFragment dialog = new EditNoteDialogFragment();
        Bundle b = new Bundle();
        b.putCharSequence(activity.getResources().getString(R.string.bundle_card_id), card.getId());
        b.putCharSequence(activity.getResources().getString(R.string.bundle_note), text);
        dialog.setArguments(b);
        dialog.show(activity.getFragmentManager(), EditNoteDialogFragment.TAG);
    }

    /**
     * Toggles the favorite state of an item
     *
     * @param card card
     */
    private void toggleFavorite(Card card, boolean favorite) {
        cardsController.toggleFavorite(context, card, favorite);

        if (activity instanceof CardsActivity)
            ((CardsActivity) activity).toggleFavorite(favorite);
        filter();
    }

    /**
     * Displays next side
     *
     * @param card card
     * @param view view of card
     */
    private void changeSide(Card card, View view) {
        final RelativeLayout rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
        final android.widget.TextView tvNumerator = (android.widget.TextView) view.findViewById(R.id.tvNumerator);

        handleQuiz(card);
        card.setSideVisible((card.getSideVisible() + 1) % card.getSide().size());
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
        if (((Side) card.getSide().get(card.getSideVisible())).contains(ComponentType.CHOICE)) {
            for (AnswerObject a : ((Choice) ((Side) card.getSide().get(0)).getFirst(ComponentType.CHOICE)).getAnswer()) {
                if (((Answer) a).isSelected()) {
                    return true;
                }
            }

            if (activity instanceof CardsActivity)
                ((CardsActivity) activity).snack(((CardsActivity) activity), R.string.select_answer, SnackBar.Style.ALERT);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if all the right answers are selected
     *
     * @param card card
     */
    private void handleQuiz(Card card) {
        Side current = (Side) card.getSide().get(card.getSideVisible());

        Side next = null;
        if (card.getSideVisible() + 1 < card.getSide().size())
            next = (Side) card.getSide().get(card.getSideVisible() + 1);

        // Handle quiz card
        if (current != null && current.contains(ComponentType.CHOICE) && next != null && next.contains(ComponentType.RESULT)) {
            // Default result
            ((Result) next.getFirst(ComponentType.RESULT)).setCorrect(true);

            for (AnswerObject a : ((Choice) current.getFirst(ComponentType.CHOICE)).getAnswer()) {
                if (a.isCorrect() != ((Answer) a).isSelected()) {
                    // At least on answer is wrong
                    ((Result) next.getFirst(ComponentType.RESULT)).setCorrect(false);
                    break;
                }
            }

            notifyDataSetChanged();
        }
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
        return cardsController.isVisible(card);
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
}