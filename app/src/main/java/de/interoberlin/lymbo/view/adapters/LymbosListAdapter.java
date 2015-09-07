package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.card.aspects.LanguageAspect;
import de.interoberlin.lymbo.model.share.MailSender;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.LymbosActivity;
import de.interoberlin.lymbo.view.dialogfragments.StackDialogFragment;

public class LymbosListAdapter extends ArrayAdapter<Lymbo> {
    // Context
    private Context context;
    private Activity activity;

    // Controllers
    private LymbosController lymbosController;
    private CardsController cardsController;

    // Filter
    private List<Lymbo> filteredItems = new ArrayList<>();
    private List<Lymbo> originalItems = new ArrayList<>();
    private LymboListFilter lymboListFilter;
    private final Object lock = new Object();

    // Properties
    private static int VIBRATION_DURATION;

    // --------------------
    // Constructors
    // --------------------

    public LymbosListAdapter(Activity activity, Context context, int resource, List<Lymbo> items) {
        super(context, resource, items);
        lymbosController = LymbosController.getInstance(activity);
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
    public View getView(int position, View v, ViewGroup parent) {
        final Lymbo lymbo = getItem(position);
        return getLymboView(position, lymbo, parent);
    }

    private View getLymboView(final int position, final Lymbo lymbo, ViewGroup parent) {
        if (lymbo.getError().isEmpty()) {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            final LinearLayout llStack = (LinearLayout) vi.inflate(R.layout.stack, parent, false);

            // Load views
            final ImageView ivImage = (ImageView) llStack.findViewById(R.id.ivImage);
            final TextView tvTitle = (TextView) llStack.findViewById(R.id.tvTitle);
            final TextView tvSubtitle = (TextView) llStack.findViewById(R.id.tvSubtitle);
            final ImageView ivShare = (ImageView) llStack.findViewById(R.id.ivShare);
            final TextView tvCardCount = (TextView) llStack.findViewById(R.id.tvCardCount);

            // Load views : bottom bar
            final LinearLayout llCategories = (LinearLayout) llStack.findViewById(R.id.llCategories);
            final TextView tvLanguageFrom = (TextView) llStack.findViewById(R.id.tvLanguageFrom);
            final TextView tvLanguageTo = (TextView) llStack.findViewById(R.id.tvLanguageTo);

            // Set values
            if (lymbo.getImage() != null && !lymbo.getImage().trim().isEmpty()) {
                Bitmap b = Base64BitmapConverter.decodeBase64(lymbo.getImage());
                ivImage.setImageBitmap(b);
            } else {
                ivImage.getLayoutParams().height = 0;
            }
            if (lymbo.getTitle() != null)
                tvTitle.setText(lymbo.getTitle());
            if (lymbo.getSubtitle() != null)
                tvSubtitle.setText(lymbo.getSubtitle());

            // Context menu
            llStack.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    if (!lymbo.isAsset()) {
                        contextMenu.add(0, 0, 0, getResources().getString(R.string.edit))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        String uuid = lymbo.getId();
                                        String title = lymbo.getTitle();
                                        String subtitle = lymbo.getSubtitle();
                                        String author = lymbo.getAuthor();
                                        String languageFrom = null;
                                        String languageTo = null;
                                        ArrayList<String> categoriesLymbo = new ArrayList<>();
                                        ArrayList<String> categoriesAll;

                                        if (lymbo.getLanguageAspect() != null && lymbo.getLanguageAspect().getFrom() != null && lymbo.getLanguageAspect().getTo() != null) {
                                            languageFrom = lymbo.getLanguageAspect().getFrom().getLangCode();
                                            languageTo = lymbo.getLanguageAspect().getTo().getLangCode();
                                        }
                                        for (Tag tag : lymbo.getCategories()) {
                                            categoriesLymbo.add(tag.getName());
                                        }

                                        categoriesAll = lymbosController.getAllCategoriesStrings();

                                        vibrate(VIBRATION_DURATION);

                                        StackDialogFragment dialog = new StackDialogFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(context.getResources().getString(R.string.bundle_lymbo_uuid), uuid);
                                        bundle.putString(context.getResources().getString(R.string.bundle_title), title);
                                        bundle.putString(context.getResources().getString(R.string.bundle_subtitle), subtitle);
                                        bundle.putString(context.getResources().getString(R.string.bundle_author), author);
                                        bundle.putString(context.getResources().getString(R.string.bundle_language_from), languageFrom);
                                        bundle.putString(context.getResources().getString(R.string.bundle_language_to), languageTo);
                                        bundle.putStringArrayList(context.getResources().getString(R.string.bundle_categories_lymbo), categoriesLymbo);
                                        bundle.putStringArrayList(context.getResources().getString(R.string.bundle_categories_all), categoriesAll);
                                        dialog.setArguments(bundle);
                                        dialog.show(activity.getFragmentManager(), "okay");
                                        return false;
                                    }
                                });
                        contextMenu.add(0, 1, 0, getResources().getString(R.string.stash_stack))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        Animation anim = ViewUtil.collapse(context, llStack);
                                        llStack.startAnimation(anim);

                                        anim.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                lymbosController.stash(lymbo);
                                                ((LymbosActivity) activity).stash(lymbo);
                                                notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                        return false;
                                    }
                                });
                    }
                }
            });

            // Tags
            for (Tag tag : lymbo.getCategories()) {
                if (!tag.getName().equals(getResources().getString(R.string.no_category))) {
                    CardView cvTag = (CardView) tag.getView(context, activity, llCategories);
                    cvTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vibrate(VIBRATION_DURATION);
                            // new SelectTagsDialogFragment().show(activity.getFragmentManager(), "okay");
                        }
                    });

                    llCategories.addView(cvTag);
                }
            }

            // Languages
            LanguageAspect languageAspect = lymbo.getLanguageAspect();
            if (languageAspect != null && languageAspect.getFrom() != null && languageAspect.getTo() != null) {
                tvLanguageFrom.setText(languageAspect.getFrom().getName(activity));
                tvLanguageTo.setText(languageAspect.getTo().getName(activity));
            } else {
                ViewUtil.remove(tvLanguageFrom);
                ViewUtil.remove(tvLanguageTo);
            }

            // Action : open cards view
            llStack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardsController.setLymbo(lymbo);
                    cardsController.init();
                    Intent openStartingPoint = new Intent(context, CardsActivity.class);
                    context.startActivity(openStartingPoint);
                }
            });

            // Action : send
            if (!lymbo.isAsset()) {
                ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MailSender.sendLymbo(context, activity, lymbo);
                    }
                });
            } else {
                ViewUtil.remove(ivShare);
            }

            // Card count
            tvCardCount.setText(String.valueOf(lymbo.getCards().size() + " " + context.getResources().getString(R.string.cards)));

            return llStack;
        } else {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            final LinearLayout llStack = (LinearLayout) vi.inflate(R.layout.stack_broken, parent, false);

            // Load views
            TextView tvTitle = (TextView) llStack.findViewById(R.id.tvTitle);
            TextView tvPath = (TextView) llStack.findViewById(R.id.tvPath);
            TextView tvError = (TextView) llStack.findViewById(R.id.tvError);

            // Set values
            tvTitle.setText(getResources().getString(R.string.broken_lymbo_file));

            if (lymbo.getPath() != null)
                tvPath.setText(lymbo.getPath());

            if (lymbo.getError() != null)
                tvError.setText(lymbo.getError());

            return llStack;
        }
    }

    public List<Lymbo> getFilteredItems() {
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
     * @param lymbo lymbo
     * @return true if item is visible
     */
    protected boolean filterLymbo(Lymbo lymbo) {
        return lymbo != null;
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
            originalItems = lymbosController.getLymbos();

            ArrayList<Lymbo> values;
            synchronized (lock) {
                values = new ArrayList<>(originalItems);
            }

            final int count = values.size();
            final ArrayList<Lymbo> newValues = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                final Lymbo value = values.get(i);
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
            filteredItems = (List<Lymbo>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}