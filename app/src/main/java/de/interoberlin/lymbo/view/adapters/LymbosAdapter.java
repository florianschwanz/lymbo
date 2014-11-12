package de.interoberlin.lymbo.view.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.view.activities.LymbosActivity;
import de.interoberlin.lymbo.view.card.DisplayStack;

public class LymbosAdapter extends RecyclerView.Adapter<LymbosAdapter.ViewHolder> {

    private Context c;
    private List<DisplayStack> dataset;
    private boolean swipeable;
    private Resources resources;

    // --------------------
    // Constructors
    // --------------------

    public LymbosAdapter(Context c, List<DisplayStack> dataset, boolean swipeable) {
        this.c = c;
        this.dataset = dataset;
        this.swipeable = swipeable;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public LymbosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_stack, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        DisplayStack ds = dataset.get(position);
        viewHolder.tvTitle.setText(ds.getTitle());
        viewHolder.tvText.setText(ds.getText());
        viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("HUHU");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }



    // --------------------
    // Inner Classes
    // --------------------

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvText;

        public ImageView ivEdit;

        // --------------------
        // Constructors
        // --------------------

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvText = (TextView) v.findViewById(R.id.tvText);
            ivEdit = (ImageView) v.findViewById(R.id.ivEdit);
        }

        // --------------------
        // Getters / Setters
        // --------------------

    }
}