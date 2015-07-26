package de.interoberlin.lymbo.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.github.mrengineer13.snackbar.SnackBar;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.view.adapters.CardsStashListAdapter;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class CardsStashActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, SnackBar.OnMessageClickListener {
    // Controllers
    CardsController cardsController;

    // Views
    private SwipeRefreshLayout srl;
    private SwipeListView slv;
    private LinearLayout toolbarWrapper;

    // Model
    private CardsStashListAdapter cardsStashAdapter;

    private Card recentCard = null;
    private int recentCardPos = -1;
    private int recentEvent = -1;

    private static final int EVENT_RESTORE = 2;

    private static int REFRESH_DELAY;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            cardsController = CardsController.getInstance(this);

            if (cardsController.getLymbo() == null) {
                finish();
            }

            setActionBarIcon(R.drawable.ic_ab_drawer);
            setDisplayHomeAsUpEnabled(true);

            REFRESH_DELAY = Integer.parseInt(Configuration.getProperty(this, EProperty.REFRESH_DELAY_CARDS));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void onResume() {
        try {
            super.onResume();
            cardsStashAdapter = new CardsStashListAdapter(this, this, R.layout.card_stash, cardsController.getCardsStashed());

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
            drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            toolbarWrapper = (LinearLayout) findViewById(R.id.toolbar_wrapper);

            srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            srl.setOnRefreshListener(this);
            srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

            slv = (SwipeListView) findViewById(R.id.slv);
            slv.setAdapter(cardsStashAdapter);
            slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

            updateSwipeRefreshProgressBarTop(srl);
            registerHideableHeaderView(toolbarWrapper);
            enableActionBarAutoHide(slv);

            // Update data
            cardsStashAdapter.updateData();

            // Update view
            updateView();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cards_stash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log: {
                Intent i = new Intent(CardsStashActivity.this, LogActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_about: {
                Intent i = new Intent(CardsStashActivity.this, AboutActivity.class);
                Bundle b = new Bundle();
                b.putString("flavor", "interoberlin");
                i.putExtras(b);
                startActivity(i);
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

        return true;
    }

    // --------------------
    // Methods - Callbacks
    // --------------------

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
                cardsController.reset();
                cardsStashAdapter.notifyDataSetChanged();
                slv.invalidateViews();
            }
        }, REFRESH_DELAY);
    }

    @Override
    public void onMessageClick(Parcelable token) {
        switch (recentEvent) {
            case EVENT_RESTORE: {
                cardsController.stash(recentCardPos, recentCard);
                break;
            }
        }

        // Update data
        cardsStashAdapter.updateData();

        // Update view
        updateView();
    }

    // --------------------
    // Methods - Actions
    // --------------------

    /**
     * Restores a card
     *
     * @param pos poistion of the card
     * @param card card to be restored
     */
    public void restore(int pos, Card card) {
        // Update view
        updateView();

        recentCard = card;
        recentCardPos = pos;
        recentEvent = EVENT_RESTORE;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.card_restored)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_cards_stash;
    }

    /**
     * Updates the view
     */
    private void updateView() {
        slv.invalidateViews();
    }

}