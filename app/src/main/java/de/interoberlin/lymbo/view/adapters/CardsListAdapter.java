package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.XmlCard;

public class CardsListAdapter extends ArrayAdapter<XmlCard> {
    Context c;
    Activity a;

    // --------------------
    // Constructors
    // --------------------

    public CardsListAdapter(Context context, Activity activity, int textViewResourceId) {
        super(context, textViewResourceId);

        this.c = context;
        this.a = activity;
    }

    public CardsListAdapter(Context context, Activity activity, int resource, List<XmlCard> items) {
        super(context, resource, items);

        this.c = context;
        this.a = activity;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        CardView cv = (CardView) vi.inflate(R.layout.card, null);

        LinearLayout llComponents = (LinearLayout) cv.findViewById(R.id.llComponents);

        for (Displayable d : getItem(position).getFront().getComponents()) {
            llComponents.addView(d.getView(c, a, llComponents));
        }

        final XmlCard lymbo = getItem(position);

        return cv;
    }
}