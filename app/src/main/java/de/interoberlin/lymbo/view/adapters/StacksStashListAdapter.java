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
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.StacksStashActivity;

public class StacksStashListAdapter extends ArrayAdapter<Stack> {
    // Context
    private Context context;
    private Activity activity;

    // View
    static class ViewHolder {
        private LinearLayout v;
        private TextView tvTitle;
        private TextView tvSubtitle;
        private ImageView ivUndo;
    }

    // Controllers
    private StacksController stacksController;

    // Filter
    // private List<Stack> filteredItems = new ArrayList<>();
    private List<Stack> originalItems = new ArrayList<>();
    private LymboListFilter lymboListFilter;
    private final Object lock = new Object();

    // --------------------
    // Constructors
    // --------------------

    public StacksStashListAdapter(Activity activity, Context context, int resource, List<Stack> items) {
        super(context, resource, items);
        stacksController = StacksController.getInstance(activity);

        // this.filteredItems = items;
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
        final Stack stack = getItem(position);

        ViewHolder viewHolder;

        if (v == null) {
            viewHolder = new ViewHolder();

            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            // Load views
            v = vi.inflate(R.layout.stack_stash, parent, false);

            viewHolder.v = (LinearLayout) v;
            viewHolder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            viewHolder.tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
            viewHolder.ivUndo = (ImageView) v.findViewById(R.id.ivUndo);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        final ViewHolder viewHolderFinal = viewHolder;

        // Set values
        if (stack.getTitle() != null)
            viewHolder.tvTitle.setText(stack.getTitle());
        if (stack.getSubtitle() != null)
            viewHolder.tvSubtitle.setText(stack.getSubtitle());

        // Action : stash
        if (stack.getPath() != null) {
            viewHolder.ivUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation anim = ViewUtil.collapse(context, viewHolderFinal.v);
                    viewHolderFinal.v.startAnimation(anim);

                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            stacksController.restore(stack);
                            ((StacksStashActivity) activity).restore(stack);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
        } else {
            ViewUtil.remove(viewHolder.v);
        }

        return v;
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
        return stack != null;
    }

    // --------------------
    // Inner classes
    // --------------------

    public class LymboListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            // Copy items
            originalItems = stacksController.getStacksStashed();

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
            // filteredItems = (List<Stack>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}