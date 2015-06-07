package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.LymbosStashActivity;

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

        if (lymbo != null) {

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
                        Animation anim = ViewUtil.collapse(c, llStack);
                        llStack.startAnimation(anim);

                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardsController.restore(lymbo);
                                ((LymbosStashActivity) a).restore(lymbo);
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
        } else {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            return vi.inflate(R.layout.toolbar_space, parent, false);
        }
    }
}