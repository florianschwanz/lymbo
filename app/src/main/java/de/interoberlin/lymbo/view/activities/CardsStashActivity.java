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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.view.adapters.CardsStashListAdapter;
import de.interoberlin.lymbo.view.dialogs.ConfirmRefreshDialog;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class CardsStashActivity extends SwipeRefreshBaseActivity implements
    // <editor-fold defaultstate="collapsed" desc="Interfaces">
        SwipeRefreshLayout.OnRefreshListener,
        ConfirmRefreshDialog.OnCompleteListener,
        SnackBar.OnMessageClickListener {
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Members">

    // View
    private CardsStashListAdapter cardsStashAdapter;
    @BindView(R.id.dl) DrawerLayout drawer;
    @BindView(R.id.toolbar_wrapper) LinearLayout toolbarWrapper;
    @BindView(R.id.toolbar_title) TextView toolbarTitleView;
    @BindView(R.id.swipe_container) SwipeRefreshLayout srl;
    @BindView(R.id.slv) SwipeListView slv;

    // Controller
    CardsController cardsController;

    private Card recentCard = null;
    private int recentCardPos = -1;
    private int recentEvent = -1;

    // Properties
    private static int REFRESH_DELAY;
    private static final int EVENT_RESTORE = 2;

    // </editor-fold>

    // --------------------
    // Methods - Lifecycle
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Lifecycle">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            cardsController = CardsController.getInstance();

            REFRESH_DELAY = getResources().getInteger(R.integer.refresh_delay_cards);

            // Restore instance state
            if (savedInstanceState != null) {
                final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
                srl.setRefreshing(true);

                String path = savedInstanceState.getString(getResources().getString(R.string.bundle_lymbo_file_name));
                boolean asset = savedInstanceState.getBoolean(getResources().getString(R.string.bundle_asset));

                cardsController.reloadStack(path, asset);
                cardsController.init(this);

                srl.setRefreshing(false);
            }

            setActionBarIcon(R.drawable.ic_arrow_back_white_24dp);
            setDisplayHomeAsUpEnabled(true);

            ButterKnife.bind(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            cardsStashAdapter = new CardsStashListAdapter(this, this, R.layout.card_stash, cardsController.getCardsStashed());

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

    // </editor-fold>

    // --------------------
    // Methods - Callbacks
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Callbacks">

    // <editor-fold defaultstate="collapsed" desc="Callbacks SwipeRefreshLayout">
    @Override
    public void onRefresh() {
        ConfirmRefreshDialog dialog = new ConfirmRefreshDialog();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.restore_cards));
        bundle.putString(getResources().getString(R.string.bundle_message), getResources().getString(R.string.restore_cards_question));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), ConfirmRefreshDialog.TAG);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks Snackbar">
    @Override
    public void onMessageClick(Parcelable token) {
        switch (recentEvent) {
            case EVENT_RESTORE: {
                cardsController.stash(this, recentCardPos, recentCard);
                break;
            }
        }

        updateListView();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks RefreshDialog">
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
    // </editor-fold>

    // --------------------
    // Methods - Actions
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Actions">

    /**
     * Restores a card
     *
     * @param pos  poistion of the card
     * @param card card to be restored
     */
    public void restore(int pos, Card card) {

        recentCard = card;
        recentCardPos = pos;
        recentEvent = EVENT_RESTORE;

        snack(this, R.string.card_restored);
        updateListView();
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Methods">

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

    // </editor-fold>

    // --------------------
    // Inner classes
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Inner classes">

    public class RestoreCardsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            cardsController.restoreAll(CardsStashActivity.this);
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

    // </editor-fold>
}