package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
import java.util.concurrent.ExecutionException;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.Language;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.objects.TagObject;
import de.interoberlin.lymbo.model.share.MailSender;
import de.interoberlin.lymbo.model.webservice.AccessControlItem;
import de.interoberlin.lymbo.model.webservice.web.LymboWebAccessControlItemTask;
import de.interoberlin.lymbo.model.webservice.web.LymboWebUploadTask;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.util.TagUtil;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.StacksActivity;
import de.interoberlin.lymbo.view.components.TagView;
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
        return getStackView(position, stack, parent);
    }

    private View getStackView(final int position, final Stack stack, ViewGroup parent) {
        if (stack.getError().isEmpty()) {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            // Load views
            final LinearLayout llStack = (LinearLayout) vi.inflate(R.layout.stack, parent, false);
            final ImageView ivImage = (ImageView) llStack.findViewById(R.id.ivImage);
            final android.widget.TextView tvTitle = (android.widget.TextView) llStack.findViewById(R.id.tvTitle);
            final android.widget.TextView tvSubtitle = (android.widget.TextView) llStack.findViewById(R.id.tvSubtitle);
            final ImageView ivShare = (ImageView) llStack.findViewById(R.id.ivShare);
            final ImageView ivUpload = (ImageView) llStack.findViewById(R.id.ivUpload);
            final android.widget.TextView tvCardCount = (android.widget.TextView) llStack.findViewById(R.id.tvCardCount);
            final LinearLayout llTags = (LinearLayout) llStack.findViewById(R.id.llTags);

            // Set values
            if (stack.getImageFormat() != null && stack.getImage() != null && !stack.getImage().trim().isEmpty()) {
                switch (stack.getImageFormat()) {
                    case BASE_64: {
                        Bitmap bmp = Base64BitmapConverter.decodeBase64(stack.getImage());
                        ivImage.setImageBitmap(bmp);
                        break;
                    }
                    case REF: {
                        String imagePath = stack.getPath() + "/" + stack.getImage();
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
            for (TagObject t : stack.getTag()) {
                if (!t.getValue().equals(getResources().getString(R.string.no_tag))) {
                    TagView cvTag = new TagView(context, (Tag) t);
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
            Language language = (Language) stack.getLanguage();
            if (language != null && language.getFrom() != null && language.getTo() != null) {
                TagView tvLanguageFrom = new TagView(context, new Tag(language.getFrom()));
                tvLanguageFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTags();
                    }
                });

                llTags.addView(tvLanguageFrom);

                TagView tvLanguageTo = new TagView(context, new Tag(language.getTo()));
                tvLanguageTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTags();
                    }
                });

                llTags.addView(tvLanguageTo);
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

            // Action : share
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

            // Action : upload
            Resources res = getResources();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String author = stack.getAuthor();
            String noAuthorSpecified = res.getString(R.string.no_author_specified);
            String username = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);

            if (author.isEmpty() || author.equals(noAuthorSpecified) || author.equals(username)) {
                ivUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Resources res = getResources();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

                        String username = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);
                        String password = prefs.getString(res.getString(R.string.pref_lymbo_web_password), null);
                        String clientId = res.getString(R.string.pref_lymbo_web_client_id);
                        String clientSecret = prefs.getString(res.getString(R.string.pref_lymbo_web_api_secret), null);

                        String id = stack.getId();
                        String author = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);
                        String content = stack.toString();

                        try {
                            AccessControlItem accessControlItem = new LymboWebAccessControlItemTask().execute(username, password, clientId, clientSecret).get();

                            if (accessControlItem != null && accessControlItem.getAccess_token() != null) {
                                new LymboWebUploadTask((StacksActivity) activity).execute(accessControlItem.getAccess_token(), id, author, content).get();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                ViewUtil.remove(ivUpload);
            }

            // Card count
            tvCardCount.setText(String.valueOf(stack.getCard().size() + " " + context.getResources().getString(R.string.cards)));

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
                tvPath.setText(stack.getFile());

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
        ArrayList<String> tagsAll = TagUtil.getDistinctValues(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = TagUtil.getDistinctValues(TagUtil.getTagList(stack.getTag()));

        if (stack.getLanguage() != null && stack.getLanguage().getFrom() != null && stack.getLanguage().getTo() != null) {
            languageFrom = stack.getLanguage().getFrom();
            languageTo = stack.getLanguage().getTo();
        }

        vibrate(VIBRATION_DURATION);

        StackDialogFragment dialog = new StackDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.edit_stack));
        bundle.putString(context.getResources().getString(R.string.bundle_lymbo_id), uuid);
        bundle.putString(context.getResources().getString(R.string.bundle_title), title);
        bundle.putString(context.getResources().getString(R.string.bundle_subtitle), subtitle);
        bundle.putString(context.getResources().getString(R.string.bundle_author), author);
        bundle.putString(context.getResources().getString(R.string.bundle_language_from), languageFrom);
        bundle.putString(context.getResources().getString(R.string.bundle_language_to), languageTo);
        bundle.putStringArrayList(context.getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(context.getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), StackDialogFragment.TAG);
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

        ArrayList<String> tagsAll = TagUtil.getDistinctValues(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = TagUtil.getDistinctValues(stacksController.getTagsSelected());

        FilterStacksDialogFragment dialog = new FilterStacksDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), FilterStacksDialogFragment.TAG);
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