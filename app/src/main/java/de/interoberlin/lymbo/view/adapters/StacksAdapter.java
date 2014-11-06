package de.interoberlin.lymbo.view.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.view.card.DisplayStack;

public class StacksAdapter extends RecyclerView.Adapter<StacksAdapter.ViewHolder> {

    private List<DisplayStack> dataset;

    // --------------------
    // Constructors
    // --------------------

    public StacksAdapter(List<DisplayStack> dataset) {
        this.dataset = dataset;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public StacksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_stack, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cardView = dataset.get(position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // --------------------
    // Inner Classes
    // --------------------

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}