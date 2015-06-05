package de.interoberlin.lymbo.util;


import android.view.View;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridLayout;

public class ViewUtil {
    private static final float DP_PER_SECOND = 0.01f;

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

    public static void expand(final View v) {
        v.measure(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

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

        // 1dp/ms
        a.setDuration((int) ((targetHeight / v.getContext().getResources().getDisplayMetrics().density) / DP_PER_SECOND));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

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

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density / DP_PER_SECOND));
        v.startAnimation(a);
    }
}
