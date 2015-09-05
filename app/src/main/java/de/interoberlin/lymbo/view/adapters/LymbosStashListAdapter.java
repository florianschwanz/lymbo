package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
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
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.LymbosStashActivity;

public class LymbosStashListAdapter extends ArrayAdapter<Lymbo> {
    // Context
    private Context context;
    private Activity activity;

    // Controllers
    private LymbosController lymbosController;

    // Filter
    private List<Lymbo> filteredItems = new ArrayList<>();
    private List<Lymbo> originalItems = new ArrayList<>();
    private LymboListFilter lymboListFilter;
    private final Object lock = new Object();

    // --------------------
    // Constructors
    // --------------------

    public LymbosStashListAdapter(Activity activity, Context context, int resource, List<Lymbo> items) {
        super(context, resource, items);
        lymbosController = LymbosController.getInstance(activity);

        this.filteredItems = items;
        this.originalItems = items;

        this.activity = activity;
        this.context = context;

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

    private View getLymboView(int position, final Lymbo lymbo, ViewGroup parent) {
        // Layout inflater
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        final LinearLayout llStack = (LinearLayout) vi.inflate(R.layout.stack_stash, parent, false);

        // Load views
        TextView tvTitle = (TextView) llStack.findViewById(R.id.tvTitle);
        TextView tvSubtitle = (TextView) llStack.findViewById(R.id.tvSubtitle);
        ImageView ivUndo = (ImageView) llStack.findViewById(R.id.ivUndo);

        // Set values
        if (lymbo.getTitle() != null)
            tvTitle.setText(lymbo.getTitle());
        if (lymbo.getSubtitle() != null)
            tvSubtitle.setText(lymbo.getSubtitle());

        // Action : stash
        if (lymbo.getPath() != null) {
            ivUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation anim = ViewUtil.collapse(context, llStack);
                    llStack.startAnimation(anim);

                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            lymbosController.restore(lymbo);
                            ((LymbosStashActivity) activity).restore(lymbo);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
        } else {
            ViewUtil.remove(ivUndo);
        }

        return llStack;
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
    // Inner classes
    // --------------------

    public class LymboListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            // Copy items
            originalItems = lymbosController.getLymbosStashed();

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