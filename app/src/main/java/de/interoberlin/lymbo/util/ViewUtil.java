package de.interoberlin.lymbo.util;


import android.content.Context;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridLayout;

import de.interoberlin.lymbo.R;

public class ViewUtil {

    // --------------------
    // Methods
    // --------------------

    /**
     * Removes a view from ViewManager
     *
     * @param v View to be removed
     */
    public static void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }

    /**
     * Expands a view by increasing its height from 0 to its target height
     *
     * @param c context
     * @param v view to expand
     * @return expand animation
     */
    public static Animation expand(final Context c, final View v) {
        v.measure(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        final int CARD_EXPAND_TIME = c.getResources().getInteger(R.integer.card_expand_time);
        // final int CARD_EXPAND_DP_PER_MILLISECOND = c.getResources().getInteger(R.integer.card_expand_dp_per_millisecond);
        // final int duration = (int) ((targetHeight / v.getContext().getResources().getDisplayMetrics().density) / CARD_EXPAND_DP_PER_MILLISECOND);

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? GridLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(CARD_EXPAND_TIME);

        return a;
    }

    public static Animation fromRight(final Context c, final View v, final int initialTranslationX) {
        v.measure(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);

        final int CARD_EXPAND_TIME = c.getResources().getInteger(R.integer.card_expand_time);
        // final int CARD_EXPAND_DP_PER_MILLISECOND = c.getResources().getInteger(R.integer.card_expand_dp_per_millisecond);
        // final int duration = (int) ((targetHeight / v.getContext().getResources().getDisplayMetrics().density) / CARD_EXPAND_DP_PER_MILLISECOND);

        v.setVisibility(View.VISIBLE);
        v.setTranslationX(initialTranslationX);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime != 1) {
                    v.setTranslationX(initialTranslationX - (int) (initialTranslationX * interpolatedTime));
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(CARD_EXPAND_TIME);

        return a;
    }

    /**
     * Collapses a view by decreasing its height
     *
     * @param c context
     * @param v view to collapse
     * @return collapse animation
     */
    public static Animation collapse(final Context c, final View v) {
        final int initialHeight = v.getMeasuredHeight();
        final int CARD_COLLAPSE_TIME = c.getResources().getInteger(R.integer.card_collapse_time);
        // final int CARD_COLLAPSE_DP_PER_MILLISECOND = c.getResources().getInteger(R.integer.card_expand_dp_per_millisecond);
        // final int duration = (int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density / CARD_COLLAPSE_DP_PER_MILLISECOND);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(CARD_COLLAPSE_TIME);

        return a;
    }
}
