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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.StacksStashActivity;

public class StacksStashListAdapter extends ArrayAdapter<Stack> {
    // <editor-fold defaultstate="collapsed" desc="Members">

    // Context
    private Context context;
    private Activity activity;

    // View
    static class ViewHolder {
        LinearLayout v;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvSubtitle) TextView tvSubtitle;
        @BindView(R.id.ivUndo) ImageView ivUndo;

        public ViewHolder(View v) {
            this.v = (LinearLayout) v;
            ButterKnife.bind(this, v);
        }
    }

    // Controllers
    private StacksController stacksController;

    // Filter
    // private List<Stack> filteredItems = new ArrayList<>();
    private List<Stack> originalItems = new ArrayList<>();
    private LymboListFilter lymboListFilter;
    private final Object lock = new Object();

    // </editor-fold>

    // --------------------
    // Constructors
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public StacksStashListAdapter(Activity activity, Context context, int resource, List<Stack> items) {
        super(context, resource, items);
        stacksController = StacksController.getInstance();

        // this.filteredItems = items;
        this.originalItems = items;

        this.activity = activity;
        this.context = context;

        filter();
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Methods">

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Stack stack = getItem(position);

        ViewHolder viewHolder;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
            viewHolder = new ViewHolder(v);
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
                            stacksController.restore(getContext(), stack);
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

    // </editor-fold>

    // --------------------
    // Methods - Filter
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Filter">

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

    // </editor-fold>

    // --------------------
    // Inner classes
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Inner classes">

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