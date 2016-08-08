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
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.view.adapters.StacksStashListAdapter;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class StacksStashActivity extends SwipeRefreshBaseActivity implements
    // <editor-fold defaultstate="collapsed" desc="Interfaces">
        SwipeRefreshLayout.OnRefreshListener,
        SnackBar.OnMessageClickListener {
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Members">

    // View
    private StacksStashListAdapter lymbosStashAdapter;
    @BindView(R.id.dl) DrawerLayout drawer;
    @BindView(R.id.toolbar_wrapper) LinearLayout toolbarWrapper;
    @BindView(R.id.toolbar_title) TextView toolbarTitleView;
    @BindView(R.id.swipe_container) SwipeRefreshLayout srl;
    @BindView(R.id.slv) SwipeListView slv;

    // Controller
    private StacksController stacksController;

    private Stack recentStack = null;

    // </editor-fold>

    // --------------------
    // Methods - Lifecycle
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Lifecycle">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stacksController = StacksController.getInstance();

        int REFRESH_DELAY = getResources().getInteger(R.integer.refresh_delay_lymbos);

        if (stacksController.getStacks().isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new LoadLymbosTask().execute();
                }
            }, REFRESH_DELAY);
        }

        setActionBarIcon(R.drawable.ic_arrow_back_white_24dp);
        setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        lymbosStashAdapter = new StacksStashListAdapter(this, this, R.layout.stack_stash, stacksController.getStacksStashed());

        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        toolbarTitleView.setText(R.string.lymbos_stash);

        srl.setOnRefreshListener(this);
        srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        srl.setEnabled(false);

        slv.setAdapter(lymbosStashAdapter);
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

        updateSwipeRefreshProgressBarTop(srl);
        registerHideableHeaderView(toolbarWrapper);
        enableActionBarAutoHide(slv);

        updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_stacks_stash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log: {
                Intent i = new Intent(StacksStashActivity.this, LogActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_about: {
                Intent i = new Intent(StacksStashActivity.this, AboutActivity.class);
                Bundle b = new Bundle();
                b.putString("flavor", "interoberlin");
                i.putExtras(b);
                startActivity(i);
                break;
            }
            case R.id.menu_settings: {
                Intent i = new Intent(StacksStashActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

        return true;
    }

    // </editor-fold>

    // --------------------
    // Methods - Callbacks
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Callbacks">

    // <editor-fold defaultstate="collapsed" desc="Callbacks SwipeRefreshLayout">

    @Override
    public void onRefresh() {
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks Snackbar">
    @Override
    public void onMessageClick(Parcelable token) {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

        stacksController.stash(this, recentStack);
        lymbosStashAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }
    // </editor-fold>

    // </editor-fold>

    // --------------------
    // Methods - Actions
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Actions">

    /**
     * Restores a lymbo
     *
     * @param stack lymbo to be restored
     */
    public void restore(Stack stack) {

        recentStack = stack;

        snack(this, R.string.stack_restored, R.string.undo);
        updateView();
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Methods">

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_stacks_stash;
    }

    /**
     * Updates the list view
     */
    private void updateView() {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

        lymbosStashAdapter.filter();
        slv.closeOpenedItems();
        slv.invalidateViews();
    }

    // </editor-fold>

    // --------------------
    // Inner classes
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Inner classes">

    public class LoadLymbosTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            stacksController.scan(StacksStashActivity.this);
            stacksController.load(StacksStashActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

            srl.setRefreshing(false);
            snack(StacksStashActivity.this, R.string.lymbos_loaded);
            updateView();
        }
    }

    // </editor-fold>
}