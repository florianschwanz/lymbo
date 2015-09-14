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
import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.StacksStashActivity;

public class StacksStashListAdapter extends ArrayAdapter<Stack> {
    // Context
    private Context context;
    private Activity activity;

    // Controllers
    private StacksController stacksController;

    // Filter
    private List<Stack> filteredItems = new ArrayList<>();
    private List<Stack> originalItems = new ArrayList<>();
    private LymboListFilter lymboListFilter;
    private final Object lock = new Object();

    // --------------------
    // Constructors
    // --------------------

    public StacksStashListAdapter(Activity activity, Context context, int resource, List<Stack> items) {
        super(context, resource, items);
        stacksController = StacksController.getInstance(activity);

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
        final Stack stack = getItem(position);
        return getLymboView(position, stack, parent);
    }

    private View getLymboView(int position, final Stack stack, ViewGroup parent) {
        // Layout inflater
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());

        // Load views
        final LinearLayout llStack = (LinearLayout) vi.inflate(R.layout.stack_stash, parent, false);
        TextView tvTitle = (TextView) llStack.findViewById(R.id.tvTitle);
        TextView tvSubtitle = (TextView) llStack.findViewById(R.id.tvSubtitle);
        ImageView ivUndo = (ImageView) llStack.findViewById(R.id.ivUndo);

        // Set values
        if (stack.getTitle() != null)
            tvTitle.setText(stack.getTitle());
        if (stack.getSubtitle() != null)
            tvSubtitle.setText(stack.getSubtitle());

        // Action : stash
        if (stack.getPath() != null) {
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
            ViewUtil.remove(ivUndo);
        }

        return llStack;
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
            originalItems = stacksController.getLymbosStashed();

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