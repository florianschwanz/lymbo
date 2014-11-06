package de.interoberlin.lymbo.view.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import de.interoberlin.lymbo.R;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<CardView> dataset;

    // --------------------
    // Constructors
    // --------------------

    public CardsAdapter(List<CardView> dataset) {
        this.dataset = dataset;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cv = dataset.get(position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // --------------------
    // Inner Classes
    // --------------------

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cv;

        public ViewHolder(CardView cv) {
            super(cv);
            this.cv = cv;
        }
    }
}