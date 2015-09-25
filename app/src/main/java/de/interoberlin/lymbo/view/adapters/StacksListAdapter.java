package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.card.aspects.LanguageAspect;
import de.interoberlin.lymbo.model.share.MailSender;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.StacksActivity;
import de.interoberlin.lymbo.view.dialogfragments.FilterStacksDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.StackDialogFragment;

public class StacksListAdapter extends ArrayAdapter<Stack> {
    // Context
    private Context context;
    private Activity activity;

    // Controllers
    private StacksController stacksController;
    private CardsController cardsController;

    // Filter
    private List<Stack> filteredItems = new ArrayList<>();
    private List<Stack> originalItems = new ArrayList<>();
    private LymboListFilter lymboListFilter;
    private final Object lock = new Object();

    // Properties
    private static int VIBRATION_DURATION;

    // --------------------
    // Constructors
    // --------------------

    public StacksListAdapter(Activity activity, Context context, int resource, List<Stack> items) {
        super(context, resource, items);
        stacksController = StacksController.getInstance(activity);
        cardsController = CardsController.getInstance(activity);

        this.filteredItems = items;
        this.originalItems = items;

        this.activity = activity;
        this.context = context;

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
    public Stack getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Stack stack = getItem(position);
        return getLymboView(position, stack, parent);
    }

    private View getLymboView(final int position, final Stack stack, ViewGroup parent) {
        if (stack.getError().isEmpty()) {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            // Load views
            final LinearLayout llStack = (LinearLayout) vi.inflate(R.layout.stack, parent, false);
            final ImageView ivImage = (ImageView) llStack.findViewById(R.id.ivImage);
            final TextView tvTitle = (TextView) llStack.findViewById(R.id.tvTitle);
            final TextView tvSubtitle = (TextView) llStack.findViewById(R.id.tvSubtitle);
            final ImageView ivShare = (ImageView) llStack.findViewById(R.id.ivShare);
            final TextView tvCardCount = (TextView) llStack.findViewById(R.id.tvCardCount);
            final LinearLayout llTags = (LinearLayout) llStack.findViewById(R.id.llTags);

            // Set values
            if (stack.getImageFormat() != null && stack.getImage() != null && !stack.getImage().trim().isEmpty()) {
                switch (stack.getImageFormat()) {
                    case BASE64: {
                        Bitmap bmp = Base64BitmapConverter.decodeBase64(stack.getImage());
                        ivImage.setImageBitmap(bmp);
                        break;
                    }
                    case REF: {
                        String imagePath = stack.getPath()+  "/" + stack.getImage();
                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                        ivImage.setImageBitmap(bmp);
                        ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                    }
                }
            } else {
                ivImage.getLayoutParams().height = 0;
            }
            if (stack.getTitle() != null)
                tvTitle.setText(stack.getTitle());
            if (stack.getSubtitle() != null)
                tvSubtitle.setText(stack.getSubtitle());

            // Context menu
            llStack.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    if (!stack.isAsset()) {
                        contextMenu.add(0, 0, 0, getResources().getString(R.string.edit))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        edit(stack);
                                        return false;
                                    }
                                });
                        contextMenu.add(0, 1, 0, getResources().getString(R.string.stash_stack))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        stash(position, stack, llStack);
                                        return false;
                                    }
                                });
                    }
                }
            });

            // Add tags
            for (Tag tag : stack.getTags()) {
                if (!tag.getName().equals(getResources().getString(R.string.no_tag))) {
                    CardView cvTag = (CardView) tag.getView(context, activity, llTags);
                    cvTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectTags();
                        }
                    });

                    llTags.addView(cvTag);
                }
            }

            // Add languages
            LanguageAspect languageAspect = stack.getLanguageAspect();
            if (languageAspect != null && languageAspect.getFrom() != null && languageAspect.getTo() != null) {
                Tag languageFromTag = new Tag(languageAspect.getFrom().getName(context));
                CardView cvTagLanguageFrom = (CardView) languageFromTag.getView(context, activity, llTags);
                cvTagLanguageFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTags();
                    }
                });

                llTags.addView(cvTagLanguageFrom);

                Tag languageToTag = new Tag(languageAspect.getTo().getName(context));
                CardView cvTagLanguageTo = (CardView) languageToTag.getView(context, activity, llTags);
                cvTagLanguageTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTags();
                    }
                });

                llTags.addView(cvTagLanguageTo);
            }

            // Action : open cards view
            llStack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardsController.setStack(stack);
                    cardsController.init();
                    Intent openStartingPoint = new Intent(context, CardsActivity.class);
                    context.startActivity(openStartingPoint);
                }
            });

            // Action : send
            if (!stack.isAsset()) {
                ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MailSender.sendLymbo(context, activity, stack);
                    }
                });
            } else {
                ViewUtil.remove(ivShare);
            }

            // Card count
            tvCardCount.setText(String.valueOf(stack.getCards().size() + " " + context.getResources().getString(R.string.cards)));

            return llStack;
        } else {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            // Load views
            final LinearLayout llStack = (LinearLayout) vi.inflate(R.layout.stack_broken, parent, false);
            TextView tvTitle = (TextView) llStack.findViewById(R.id.tvTitle);
            TextView tvPath = (TextView) llStack.findViewById(R.id.tvPath);
            TextView tvError = (TextView) llStack.findViewById(R.id.tvError);

            // Set values
            tvTitle.setText(getResources().getString(R.string.broken_lymbo_file));

            // Context menu
            llStack.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    if (!stack.isAsset()) {
                        contextMenu.add(0, 0, 0, getResources().getString(R.string.stash_stack))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        stash(position, stack, llStack);
                                        return false;
                                    }
                                });
                    }
                }
            });

            if (stack.getPath() != null)
                tvPath.setText(stack.getPath());

            if (stack.getError() != null)
                tvError.setText(stack.getError());

            return llStack;
        }
    }

    // --------------------
    // Methods - Actions
    // --------------------

    /**
     * Opens dialog to edit stack
     *
     * @param stack stack to be edited
     */
    private void edit(final Stack stack) {
        String uuid = stack.getId();
        String title = stack.getTitle();
        String subtitle = stack.getSubtitle();
        String author = stack.getAuthor();
        String languageFrom = null;
        String languageTo = null;
        ArrayList<String> tagsAll = Tag.getNames(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = Tag.getNames(stack.getTags());

        if (stack.getLanguageAspect() != null && stack.getLanguageAspect().getFrom() != null && stack.getLanguageAspect().getTo() != null) {
            languageFrom = stack.getLanguageAspect().getFrom().getLangCode();
            languageTo = stack.getLanguageAspect().getTo().getLangCode();
        }

        vibrate(VIBRATION_DURATION);

        StackDialogFragment dialog = new StackDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.edit_stack));
        bundle.putString(context.getResources().getString(R.string.bundle_lymbo_uuid), uuid);
        bundle.putString(context.getResources().getString(R.string.bundle_title), title);
        bundle.putString(context.getResources().getString(R.string.bundle_subtitle), subtitle);
        bundle.putString(context.getResources().getString(R.string.bundle_author), author);
        bundle.putString(context.getResources().getString(R.string.bundle_language_from), languageFrom);
        bundle.putString(context.getResources().getString(R.string.bundle_language_to), languageTo);
        bundle.putStringArrayList(context.getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(context.getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), "okay");
    }

    /**
     * Performs stash animation
     *
     * @param position position of item
     * @param stack    stack to be stashed
     * @param llStack  corresponding view
     */
    private void stash(final int position, final Stack stack, final LinearLayout llStack) {
        Animation a = ViewUtil.collapse(context, llStack);
        llStack.startAnimation(a);

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                stacksController.stash(stack);
                ((StacksActivity) activity).stash(position, stack);
                filter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * Opens a dialog to select tags
     */
    private void selectTags() {
        ((Vibrator) activity.getSystemService(Activity.VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

        ArrayList<String> tagsAll = Tag.getNames(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = Tag.getNames(stacksController.getTagsSelected());

        FilterStacksDialogFragment dialog = new FilterStacksDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), "okay");
    }

    // --------------------
    // Methods - Filter
    // --------------------

    public List<Stack> getFilteredItems() {
        return filteredItems;
    }

    public void filter() {
        getFilter().filter("");
    }

    @Override
    public Filter getFilter() {
        if (lymboListFilter == null) {
            lymboListFilter = new LymboListFilter();
        }
        return lymboListFilter;
    }

    /**
     * Determines if a lymbo shall be displayed
     *
     * @param stack lymbo
     * @return true if item is visible
     */
    protected boolean filterLymbo(Stack stack) {
        return stacksController.isVisible(stack);
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

    public class LymboListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            // Copy items
            originalItems = stacksController.getStacks();

            ArrayList<Stack> values;
            synchronized (lock) {
                values = new ArrayList<>(originalItems);
            }

            final int count = values.size();
            final ArrayList<Stack> newValues = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                final Stack value = values.get(i);
                if (filterLymbo(value)) {
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
            filteredItems = (List<Stack>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}