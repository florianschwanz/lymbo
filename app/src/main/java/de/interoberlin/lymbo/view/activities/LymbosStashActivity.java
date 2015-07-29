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
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.view.adapters.LymbosStashListAdapter;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class LymbosStashActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, SnackBar.OnMessageClickListener {
    // Controllers
    LymbosController lymbosController;
    CardsController cardsController;

    // Views
    private SwipeRefreshLayout srl;
    private SwipeListView slv;
    private LinearLayout toolbarWrapper;
    private TextView toolbarTitleView;

    // Model
    private List<Lymbo> lymbos;
    private LymbosStashListAdapter lymbosStashAdapter;

    private Lymbo recentLymbo = null;

    private static int REFRESH_DELAY;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lymbosController = LymbosController.getInstance(this);
        cardsController = CardsController.getInstance(this);

        if (savedInstanceState != null) {
            lymbosController.load();
        }

        setActionBarIcon(R.drawable.ic_ab_drawer);
        setDisplayHomeAsUpEnabled(true);

        REFRESH_DELAY = Integer.parseInt(Configuration.getProperty(this, EProperty.REFRESH_DELAY_LYMBOS));
    }

    public void onResume() {
        super.onResume();
        lymbos = lymbosController.getLymbosStashed();
        lymbosStashAdapter = new LymbosStashListAdapter(this, this, R.layout.stack_stash, lymbos);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        toolbarWrapper = (LinearLayout) findViewById(R.id.toolbar_wrapper);
        toolbarTitleView = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleView.setText(R.string.lymbos_stash);

        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        srl.setOnRefreshListener(this);
        srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        slv = (SwipeListView) findViewById(R.id.slv);
        slv.setAdapter(lymbosStashAdapter);
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

        updateSwipeRefreshProgressBarTop(srl);
        registerHideableHeaderView(toolbarWrapper);
        enableActionBarAutoHide(slv);

        // Update view
        updateView();
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
        getMenuInflater().inflate(R.menu.activity_lymbos_stash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log: {
                Intent i = new Intent(LymbosStashActivity.this, LogActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_about: {
                Intent i = new Intent(LymbosStashActivity.this, AboutActivity.class);
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
    public void onMessageClick(Parcelable token) {
        cardsController.stash(recentLymbo);
        lymbosStashAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);

                lymbosController.scan();
                lymbosController.load();

                lymbosStashAdapter.notifyDataSetChanged();
                slv.invalidateViews();
            }
        }, REFRESH_DELAY);
    }

    // --------------------
    // Methods - Actions
    // --------------------

    /**
     * Restores a lymbo
     *
     * @param lymbo lymbo to be restored
     */
    public void restore(Lymbo lymbo) {
        slv.invalidateViews();

        recentLymbo = lymbo;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.stack_restored)
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
        return R.layout.activity_lymbos_stash;
    }

    /**
     * Updates the view
     */
    private void updateView() {
        slv.invalidateViews();
    }
}