package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;

public class LymbosStashListAdapter extends ArrayAdapter<Lymbo> {
    Context c;
    Activity a;

    // Controllers
    CardsController cardsController = CardsController.getInstance();


    // --------------------
    // Constructors
    // --------------------

    public LymbosStashListAdapter(Activity activity, Context context, int resource, List<Lymbo> items) {
        super(context, resource, items);
        this.a = activity;
        this.c = context;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Lymbo lymbo = getItem(position);

        // Layout inflater
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        LinearLayout ll = (LinearLayout) vi.inflate(R.layout.stack_stash, null);

        // Load views
        TextView tvTitle = (TextView) ll.findViewById(R.id.tvTitle);
        TextView tvSubtitle = (TextView) ll.findViewById(R.id.tvSubtitle);
        ImageView ivUndo = (ImageView) ll.findViewById(R.id.ivUndo);

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
                    cardsController.setLymbo(lymbo);

                    DisplayDialogFragment inputDialogFragment = new DisplayDialogFragment();
                    Bundle b = new Bundle();
                    b.putString("type", EDialogType.RESTORE_STACK.toString());
                    b.putString("title", c.getResources().getString(R.string.restore_stack));
                    b.putString("message", c.getResources().getString(R.string.restore_stack_question));
                    b.putString("hint", "");

                    inputDialogFragment.setArguments(b);
                    inputDialogFragment.show(a.getFragmentManager(), "okay");
                    notifyDataSetChanged();
                }
            });
        } else {
            remove(ivUndo);
        }

        return ll;
    }

    private void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }
}