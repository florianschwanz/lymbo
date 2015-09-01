package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
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
import de.interoberlin.lymbo.model.card.aspects.LanguageAspect;
import de.interoberlin.lymbo.model.share.MailSender;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.LymbosActivity;
import de.interoberlin.lymbo.view.dialogfragments.EditStackDialogFragment;

public class LymbosListAdapter extends ArrayAdapter<Lymbo> {
    // Context
    private Context c;
    private Activity a;

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

        this.a = activity;
        this.c = context;

        // Properties
        VIBRATION_DURATION = Integer.parseInt(Configuration.getProperty(c, EProperty.VIBRATION_DURATION));

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
            ImageView ivImage = (ImageView) llStack.findViewById(R.id.ivImage);
            TextView tvTitle = (TextView) llStack.findViewById(R.id.tvTitle);
            TextView tvSubtitle = (TextView) llStack.findViewById(R.id.tvSubtitle);
            ImageView ivShare = (ImageView) llStack.findViewById(R.id.ivShare);
            TextView tvCardCount = (TextView) llStack.findViewById(R.id.tvCardCount);
            TextView tvLanguageFrom = (TextView) llStack.findViewById(R.id.tvLanguageFrom);
            TextView tvLanguageTo = (TextView) llStack.findViewById(R.id.tvLanguageTo);

            // Set values
            if (lymbo.getImage() != null && !lymbo.getImage().trim().isEmpty()) {
                Bitmap b = Base64BitmapConverter.decodeBase64(lymbo.getImage());
                BitmapDrawable bd = new BitmapDrawable(b);
                ivImage.setBackgroundDrawable(bd);
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
                        contextMenu.add(0, 0, 0, a.getResources().getString(R.string.edit))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        String uuid = lymbo.getId();
                                        String title = lymbo.getTitle();
                                        String subtitle = lymbo.getSubtitle();
                                        String author = lymbo.getAuthor();
                                        String languageFrom = null;
                                        String languageTo = null;

                                        if (lymbo.getLanguageAspect() != null) {
                                            languageFrom = lymbo.getLanguageAspect().getFrom().getLangCode();
                                            languageTo = lymbo.getLanguageAspect().getTo().getLangCode();
                                        }

                                        ((Vibrator) a.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
                                        EditStackDialogFragment dialog = new EditStackDialogFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(c.getResources().getString(R.string.bundle_lymbo_uuid), uuid);
                                        bundle.putString(c.getResources().getString(R.string.bundle_title), title);
                                        bundle.putString(c.getResources().getString(R.string.bundle_subtitle), subtitle);
                                        bundle.putString(c.getResources().getString(R.string.bundle_author), author);
                                        bundle.putString(c.getResources().getString(R.string.bundle_language_from), languageFrom);
                                        bundle.putString(c.getResources().getString(R.string.bundle_language_to), languageTo);
                                        dialog.setArguments(bundle);
                                        dialog.show(a.getFragmentManager(), "okay");
                                        return false;
                                    }
                                });
                        contextMenu.add(0, 1, 0, a.getResources().getString(R.string.stash_stack))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        Animation anim = ViewUtil.collapse(c, llStack);
                                        llStack.startAnimation(anim);

                                        anim.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                lymbosController.stash(lymbo);
                                                ((LymbosActivity) a).stash(position, lymbo);
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

            // Languages
            LanguageAspect languageAspect = lymbo.getLanguageAspect();
            if (languageAspect != null && languageAspect.getFrom() != null && languageAspect.getTo() != null) {
                tvLanguageFrom.setText(languageAspect.getFrom().getName(a));
                tvLanguageTo.setText(languageAspect.getTo().getName(a));
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
                    Intent openStartingPoint = new Intent(c, CardsActivity.class);
                    c.startActivity(openStartingPoint);
                }
            });

            // Action : send
            if (!lymbo.isAsset()) {
                ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MailSender.sendLymbo(c, a, lymbo);
                    }
                });
            } else {
                ViewUtil.remove(ivShare);
            }

            // Card count
            tvCardCount.setText(String.valueOf(lymbo.getCards().size() + " " + c.getResources().getString(R.string.cards)));

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
            tvTitle.setText(a.getResources().getString(R.string.broken_lymbo_file));

            if (lymbo.getPath() != null)
                tvPath.setText(lymbo.getPath());

            if (lymbo.getError() != null)
                tvError.setText(lymbo.getError());

            return llStack;
        }
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