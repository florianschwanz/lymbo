/*
 * Copyright (C) 2014 Antonio Leiva Gordillo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.interoberlin.lymbo.view.activities;


import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import de.interoberlin.lymbo.R;

public abstract class SwipeRefreshBaseActivity extends BaseActivity {
    // Views

    // Toolbar
    private ArrayList<View> hideableHeaderViews = new ArrayList<>();
    private ArrayList<View> hideableFooterViews = new ArrayList<>();

    // private boolean actionBarAutoHideEnabled = false;
    private int actionBarAutoHideSensivity = 0;
    private int actionBarAutoHideMinY = 0;
    private int actionBarAutoHideSignal = 0;
    private boolean actionBarShown = true;

    private static final int HEADER_HIDE_ANIM_DURATION = 500;

    // --------------------
    // Methods
    // --------------------

    // --------------------
    // Methods -  Toolbar
    // --------------------

    /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private void initActionBarAutoHide() {
        // actionBarAutoHideEnabled = true;
        actionBarAutoHideMinY = getResources().getDimensionPixelSize(
                R.dimen.toolbar_auto_hide_min_y);
        actionBarAutoHideSensivity = getResources().getDimensionPixelSize(
                R.dimen.toolbar_auto_hide_sensivity);
    }

    protected void enableActionBarAutoHide(final ListView listView) {
        initActionBarAutoHide();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            final static int ITEMS_THRESHOLD = 1;
            int lastFvi = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                        lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                                lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
                );
                lastFvi = firstVisibleItem;
            }
        });
    }

    /**
     * Indicates that the main content has scrolled (for the purposes of showing/hiding
     * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
     * (if the underlying view supports it) or may be approximate indications:
     * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
     * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
     * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
     */
    private void onMainContentScrolled(int currentY, int deltaY) {
        if (deltaY > actionBarAutoHideSensivity) {
            deltaY = actionBarAutoHideSensivity;
        } else if (deltaY < -actionBarAutoHideSensivity) {
            deltaY = -actionBarAutoHideSensivity;
        }

        if (Math.signum(deltaY) * Math.signum(actionBarAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            actionBarAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            actionBarAutoHideSignal += deltaY;
        }

        boolean shouldShow = currentY < actionBarAutoHideMinY ||
                (actionBarAutoHideSignal <= -actionBarAutoHideSensivity);
        autoShowOrHideActionBar(shouldShow);
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == actionBarShown) {
            return;
        }

        actionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {
        for (View view : hideableHeaderViews) {
            if (shown) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                view.animate()
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }

        for (View view : hideableFooterViews) {
            if (shown) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                view.animate()
                        .translationY(view.getTop())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
    }

    protected void updateSwipeRefreshProgressBarTop(SwipeRefreshLayout srl) {
        if (srl == null) {
            return;
        }

        int progressBarStartMargin = getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_start_margin);
        int progressBarEndMargin = getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_end_margin);
        int progressBarTopWhenActionBarShown = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        int top = actionBarShown ? progressBarTopWhenActionBarShown : 0;
        srl.setProgressViewOffset(false, top + progressBarStartMargin, top + progressBarEndMargin);
    }

    protected void registerHideableHeaderView(View hideableHeaderView) {
        if (!hideableHeaderViews.contains(hideableHeaderView)) {
            hideableHeaderViews.add(hideableHeaderView);
        }
    }

    protected void deregisterHideableHeaderView(View hideableHeaderView) {
        if (hideableHeaderViews.contains(hideableHeaderView)) {
            hideableHeaderViews.remove(hideableHeaderView);
        }
    }

    protected void registerHideableFooterView(View hideableFooterView) {
        if (!hideableFooterViews.contains(hideableFooterView)) {
            hideableFooterViews.add(hideableFooterView);
        }
    }

    protected void deregisterHideableFooterView(View hideableFooterView) {
        if (hideableFooterViews.contains(hideableFooterView)) {
            hideableFooterViews.remove(hideableFooterView);
        }
    }
}
