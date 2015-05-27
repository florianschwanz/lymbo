package de.interoberlin.lymbo.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.github.mrengineer13.snackbar.SnackBar;

import java.util.ArrayList;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.view.adapters.LymbosListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;

public class LymbosActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, SnackBar.OnMessageClickListener, DisplayDialogFragment.OnCompleteListener {
    // Controllers
    private LymbosController lymbosController = LymbosController.getInstance();
    private CardsController cardsController = CardsController.getInstance();

    // Views
    private SwipeRefreshLayout srl;
    private SwipeListView slv;
    private LinearLayout toolbarWrapper;

    // Model
    private LymbosListAdapter lymbosAdapter;

    // Toolbar
    private ArrayList<View> hideableHeaderViews = new ArrayList<>();
    private ArrayList<View> hideableFooterViews = new ArrayList<>();

    private boolean actionBarAutoHideEnabled = false;
    private int actionBarAutoHideSensivity = 0;
    private int actionBarAutoHideMinY = 0;
    private int actionBarAutoHideSignal = 0;
    private boolean actionBarShown = true;

    private static final int HEADER_HIDE_ANIM_DURATION = 300;

    private static int REFRESH_DELAY;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lymbosController.load();
        }

        setActionBarIcon(R.drawable.ic_ab_drawer);
        setDisplayHomeAsUpEnabled(false);

        REFRESH_DELAY = Integer.parseInt(Configuration.getProperty(this, EProperty.REFRESH_DELAY));
    }

    public void onResume() {
        super.onResume();
        lymbosAdapter = new LymbosListAdapter(this, this, R.layout.stack, lymbosController.getLymbos());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        toolbarWrapper = (LinearLayout) findViewById(R.id.toolbar_wrapper);

        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        srl.setOnRefreshListener(this);
        srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        slv = (SwipeListView) findViewById(R.id.slv);
        slv.setAdapter(lymbosAdapter);
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

        updateSwipeRefreshProgressBarTop();
        registerHideableHeaderView(toolbarWrapper);
        enableActionBarAutoHide(slv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lymbos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_stash: {
                Intent i = new Intent(LymbosActivity.this, LymbosStashActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_log: {
                Intent i = new Intent(LymbosActivity.this, LogActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_about: {
                Intent i = new Intent(LymbosActivity.this, AboutActivity.class);
                Bundle b = new Bundle();
                b.putString("flavor", "interoberlin");
                i.putExtras(b);
                startActivity(i);
                break;
            }
            /*
            case R.id.menu_add: {
                InputDialogFragment inputDialogFragment = new InputDialogFragment();
                Bundle b = new Bundle();
                b.putString("type", "CREATE_STACK");
                b.putString("title", a.getResources().getString(R.string.txtNewStack));
                b.putString("message", a.getResources().getString(R.string.txtChooseName));
                b.putString("hint", "");

                inputDialogFragment.setArguments(b);
                inputDialogFragment.show(a.getFragmentManager(), "okay");

                break;
            }
            case R.id.menu_refresh: {
                findLymboFiles();
                clear();
                draw();
                break;
            }
            case R.id.menu_download: {
                InputDialogFragment inputDialogFragment = new InputDialogFragment();
                Bundle b = new Bundle();
                b.putString("type", "DOWNLOAD_BLOB");
                b.putString("title", a.getResources().getString(R.string.txtDownloadFromBlob));
                b.putString("message", a.getResources().getString(R.string.txtEnterBlobCode));
                b.putString("hint", "");

                inputDialogFragment.setArguments(b);
                inputDialogFragment.show(a.getFragmentManager(), "okay");

                break;
            }
            */
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

        return true;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);

                lymbosController.scan();
                lymbosController.load();

                lymbosAdapter.notifyDataSetChanged();
                slv.invalidateViews();
            }
        }, REFRESH_DELAY);
    }

    /**
     * Stashes the current lymbo
     */
    public void stash() {
        cardsController.stash();
        lymbosAdapter.notifyDataSetChanged();
        slv.invalidateViews();

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.stack_stashed)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    @Override
    public void onMessageClick(Parcelable token) {
        cardsController.restore();
        lymbosAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    // --------------------
    // Methods - Callbacks
    // --------------------

    @Override
    public void onHintDialogComplete() {

    }

    @Override
    public void onDiscardCardDialogComplete() {

    }

    // --------------------
    // Methods -  Toolbar
    // --------------------

    /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private void initActionBarAutoHide() {
        actionBarAutoHideEnabled = true;
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
        updateSwipeRefreshProgressBarTop();

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
    }

    private void updateSwipeRefreshProgressBarTop() {
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

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_lymbos;
    }

    /*
    public void uiRefresh() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear();
                draw();
            }
        });
    }
    */
}