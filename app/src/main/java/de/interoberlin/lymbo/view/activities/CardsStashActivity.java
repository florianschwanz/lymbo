package de.interoberlin.lymbo.view.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.view.adapters.CardsStashListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.ConfirmRefreshDialogFragment;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class CardsStashActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, ConfirmRefreshDialogFragment.OnCompleteListener, SnackBar.OnMessageClickListener {
    // Controllers
    CardsController cardsController;

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

            REFRESH_DELAY = getResources().getInteger(R.integer.refresh_delay_cards);

            // Restore instance state
            if (savedInstanceState != null) {
                final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
                srl.setRefreshing(true);

                String path = savedInstanceState.getString(getResources().getString(R.string.bundle_lymbo_file_name));
                boolean asset = savedInstanceState.getBoolean(getResources().getString(R.string.bundle_asset));

                cardsController.reloadStack(path, asset);
                cardsController.init();

                srl.setRefreshing(false);
            }

            setActionBarIcon(R.drawable.ic_ab_drawer);
            setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void onResume() {
        try {
            super.onResume();
            cardsStashAdapter = new CardsStashListAdapter(this, this, R.layout.card_stash, cardsController.getCardsStashed());

            // Load layout
            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
            final LinearLayout toolbarWrapper = (LinearLayout) findViewById(R.id.toolbar_wrapper);
            final TextView toolbarTitleView = (TextView) findViewById(R.id.toolbar_title);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

            toolbarTitleView.setText(R.string.cards_stash);

            drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            srl.setOnRefreshListener(this);
            srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

            slv.setAdapter(cardsStashAdapter);
            slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

            updateSwipeRefreshProgressBarTop(srl);
            registerHideableHeaderView(toolbarWrapper);
            enableActionBarAutoHide(slv);

            updateListView();
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
            case R.id.menu_settings: {
                Intent i = new Intent(CardsStashActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(getResources().getString(R.string.bundle_lymbo_file_name), cardsController.getStack().getPath());
        savedInstanceState.putBoolean(getResources().getString(R.string.bundle_asset), cardsController.getStack().isAsset());

        super.onSaveInstanceState(savedInstanceState);
    }

    // --------------------
    // Methods - Callbacks
    // --------------------

    @Override
    public void onRefresh() {
        ConfirmRefreshDialogFragment dialog = new ConfirmRefreshDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.restore_cards));
        bundle.putString(getResources().getString(R.string.bundle_message), getResources().getString(R.string.restore_cards_question));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), ConfirmRefreshDialogFragment.TAG);
    }

    @Override
    public void onConfirmRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new RestoreCardsTask().execute();
            }
        }, REFRESH_DELAY);
    }

    @Override
    public void onCancelRefresh() {
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        srl.setRefreshing(false);
    }

    @Override
    public void onMessageClick(Parcelable token) {
        switch (recentEvent) {
            case EVENT_RESTORE: {
                cardsController.stash(recentCardPos, recentCard);
                break;
            }
        }

        updateListView();
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

        recentCard = card;
        recentCardPos = pos;
        recentEvent = EVENT_RESTORE;

        snack(this, R.string.card_restored);
        updateListView();
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_cards_stash;
    }

    /**
     * Updates the list view
     */
    private void updateListView() {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

        cardsStashAdapter.filter();
        slv.closeOpenedItems();
        slv.invalidateViews();
    }

    // --------------------
    // Inner classes
    // --------------------

    public class RestoreCardsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            cardsController.restoreAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

            srl.setRefreshing(false);
            snack(CardsStashActivity.this, R.string.cards_unstashed);
            updateListView();
        }
    }
}