package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.CardsStashActivity;

public class CardsStashListAdapter extends ArrayAdapter<Card> {
    private Context c;
    private Activity a;

    // Controllers
    CardsController cardsController = CardsController.getInstance();
    // ComponentsController componentsController = ComponentsController.getInstance();

    // --------------------
    // Constructors
    // --------------------

    public CardsStashListAdapter(Context context, Activity activity, int resource, List<Card> items) {
        super(context, resource, items);

        this.c = context;
        this.a = activity;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final Card card = getItem(position);

        return getCardView(position, card, parent);
    }

    private View getCardView(final int position, final Card card, final ViewGroup parent) {
        if (card != null) {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            final FrameLayout flCard = (FrameLayout) vi.inflate(R.layout.card_stash, parent, false);

            // Load views : components
            final RelativeLayout rlMain = (RelativeLayout) flCard.findViewById(R.id.rlMain);

            // Load views : bottom bar
            final LinearLayout llTags = (LinearLayout) flCard.findViewById(R.id.llTags);
            final ImageView ivUndo = (ImageView) flCard.findViewById(R.id.ivUndo);

            // Add sides
            for (Side side : card.getSides()) {
                View component = side.getView(c, a, rlMain);
                component.setVisibility(View.INVISIBLE);
                rlMain.addView(component);
            }

            // Display width
            DisplayMetrics displaymetrics = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final int displayWidth = displaymetrics.widthPixels;

            rlMain.getChildAt(card.getSideVisible()).setVisibility(View.VISIBLE);

            // Tags
            for (Tag tag : card.getTags()) {
                if (!tag.getName().equals(c.getResources().getString(R.string.no_tag)))
                    llTags.addView(tag.getView(c, a, llTags));
            }

            // Reveal : undo
            ivUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation anim = ViewUtil.toLeft(c, flCard, displayWidth);
                    flCard.startAnimation(anim);

                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            cardsController.restore(card.getId());
                            ((CardsStashActivity) a).restore(position, card.getId());
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            if (card.isRestoring()) {
                flCard.setTranslationX(displayWidth);

                Animation anim = ViewUtil.expand(c, flCard);
                flCard.startAnimation(anim);

                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        card.setRestoring(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation anim = ViewUtil.fromLeft(c, flCard, displayWidth);
                        flCard.startAnimation(anim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            return flCard;
        } else {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            return vi.inflate(R.layout.toolbar_space, parent, false);
        }
    }

    /**
     * Stashes card
     *
     * @param uuid   position of item
     * @param flCard
     */
    private void stash(int pos, String uuid, FrameLayout flCard) {
        ViewUtil.collapse(c, flCard);

        cardsController.stash(uuid);
        ((CardsActivity) a).stash(pos, uuid);
        notifyDataSetChanged();
    }
}