package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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
import de.interoberlin.lymbo.model.share.MailSender;
import de.interoberlin.lymbo.model.webservice.AccessControlItem;
import de.interoberlin.lymbo.model.webservice.web.LymboWebAccessControlItemTask;
import de.interoberlin.lymbo.model.webservice.web.LymboWebUploadTask;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.StacksActivity;
import de.interoberlin.lymbo.view.components.TagView;
import de.interoberlin.lymbo.view.dialogs.FilterStacksDialog;
import de.interoberlin.lymbo.view.dialogs.StackDialog;

public class StacksListAdapter extends ArrayAdapter<Stack> {
    // Context
    private Context context;
    private Activity activity;

    // View
    static class ViewHolder {
        private LinearLayout v;
        private ImageView ivImage;
        private TextView tvTitle;
        private TextView tvSubtitle;
        private ImageView ivShare;
        private ImageView ivUpload;
        private TextView tvCardCount;
        private LinearLayout llTags;

        private TextView tvPath;
        private TextView tvError;
    }

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
    public View getView(final int position, View v, ViewGroup parent) {
        final Stack stack = getItem(position);

        ViewHolder viewHolder;

        if (stack.getError() == null) {
            if (v == null) {
                viewHolder = new ViewHolder();

                // Layout inflater
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());

                // Load views
                v = vi.inflate(R.layout.stack, parent, false);

                viewHolder.v = (LinearLayout) v;
                viewHolder.ivImage = (ImageView) v.findViewById(R.id.ivImage);
                viewHolder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                viewHolder.tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
                viewHolder.ivShare = (ImageView) v.findViewById(R.id.ivShare);
                viewHolder.ivUpload = (ImageView) v.findViewById(R.id.ivUpload);
                viewHolder.tvCardCount = (TextView) v.findViewById(R.id.tvCardCount);
                viewHolder.llTags = (LinearLayout) v.findViewById(R.id.llTags);

                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            final ViewHolder viewHolderFinal = viewHolder;

            // Tint
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.ivShare.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
                viewHolder.ivUpload.getDrawable().setTint(ContextCompat.getColor(context, R.color.card_icon));
            }

            // Set values
            if (stack.getImageFormat() != null && stack.getImage() != null && !stack.getImage().trim().isEmpty()) {
                switch (stack.getImageFormat()) {
                    case BASE_64: {
                        Bitmap bmp = Base64BitmapConverter.decodeBase64(stack.getImage());
                        viewHolder.ivImage.setImageBitmap(bmp);
                        break;
                    }
                    case REF: {
                        String imagePath = stack.getPath() + "/" + stack.getImage();
                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                        viewHolder.ivImage.setImageBitmap(bmp);
                        viewHolder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                    }
                }
            } else {
                viewHolder.ivImage.getLayoutParams().height = 0;
            }

            if (stack.getTitle() != null)
                viewHolder.tvTitle.setText(stack.getTitle());
            if (stack.getSubtitle() != null)
                viewHolder.tvSubtitle.setText(stack.getSubtitle());

            // Context menu
            viewHolder.v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
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
                                        stash(position, stack, viewHolderFinal.v);
                                        return false;
                                    }
                                });
                    }
                }
            });

            // Add tags
            for (Tag tag : stack.getTags()) {
                if (!tag.getValue().equals(getResources().getString(R.string.no_tag))) {
                    TagView cvTag = new TagView(context, tag);
                    cvTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectTags();
                        }
                    });

                    viewHolder.llTags.addView(cvTag);
                }
            }

            // Add languages
            Language language = stack.getLanguage();
            if (language != null && language.getFrom() != null && language.getTo() != null) {
                Tag languageFromTag = new Tag(language.getFrom());
                TagView cvTagLanguageFrom = new TagView(context, languageFromTag);
                cvTagLanguageFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTags();
                    }
                });

                viewHolder.llTags.addView(cvTagLanguageFrom);

                Tag languageToTag = new Tag(language.getTo());
                TagView cvTagLanguageTo = new TagView(context, languageToTag);
                cvTagLanguageTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectTags();
                    }
                });

                viewHolder.llTags.addView(cvTagLanguageTo);
            }

            // Action : open cards view
            viewHolder.v.setOnClickListener(new View.OnClickListener() {
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
                viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MailSender.sendLymbo(context, activity, stack);
                    }
                });
            } else {
                ViewUtil.remove(viewHolder.ivShare);
            }

            // Action : upload
            Resources res = getResources();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String author = stack.getAuthor();
            String noAuthorSpecified = res.getString(R.string.no_author_specified);
            String username = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);

            if (author == null || author.isEmpty() || author.equals(noAuthorSpecified) || author.equals(username)) {
                viewHolder.ivUpload.setOnClickListener(new View.OnClickListener() {
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
                ViewUtil.remove(viewHolder.ivUpload);
            }

            // Card count
            viewHolder.tvCardCount.setText(String.valueOf(stack.getCards().size() + " " + context.getResources().getString(R.string.cards)));

            return v;
        } else {
            if (v == null) {
                viewHolder = new ViewHolder();
                // Layout inflater
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());

                // Load views
                v = vi.inflate(R.layout.stack_broken, parent, false);

                viewHolder.v = (LinearLayout) v;
                viewHolder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                viewHolder.tvPath = (TextView) v.findViewById(R.id.tvPath);
                viewHolder.tvError = (TextView) v.findViewById(R.id.tvError);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            final ViewHolder viewHolderFinal = viewHolder;

            // Set values
            viewHolder.tvTitle.setText(getResources().getString(R.string.broken_lymbo_file));

            // Context menu
            viewHolder.v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    if (!stack.isAsset()) {
                        contextMenu.add(0, 0, 0, getResources().getString(R.string.stash_stack))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        stash(position, stack, viewHolderFinal.v);
                                        return false;
                                    }
                                });
                    }
                }
            });

            if (stack.getPath() != null)
                viewHolderFinal.tvPath.setText(stack.getFile());

            if (stack.getError() != null)
                viewHolderFinal.tvError.setText(stack.getError());

            return v;
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
        ArrayList<String> tagsAll = Tag.getValues(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = Tag.getValues(stack.getTags());

        if (stack.getLanguage() != null && stack.getLanguage().getFrom() != null && stack.getLanguage().getTo() != null) {
            languageFrom = stack.getLanguage().getFrom();
            languageTo = stack.getLanguage().getTo();
        }

        vibrate(VIBRATION_DURATION);

        StackDialog dialog = new StackDialog();
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
        dialog.show(activity.getFragmentManager(), StackDialog.TAG);
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

        ArrayList<String> tagsAll = Tag.getValues(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = Tag.getValues(stacksController.getTagsSelected());

        FilterStacksDialog dialog = new FilterStacksDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(activity.getFragmentManager(), FilterStacksDialog.TAG);
    }

    // --------------------
    // Methods - Filter
    // --------------------

    /*
    public List<Stack> getFilteredItems() {
        return filteredItems;
    }
    */

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