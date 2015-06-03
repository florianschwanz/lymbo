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
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.github.mrengineer13.snackbar.SnackBar;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.view.adapters.LymbosListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.AddStackDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;

public class LymbosActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, AddStackDialogFragment.OnCompleteListener, SnackBar.OnMessageClickListener {
    // Controllers
    private LymbosController lymbosController = LymbosController.getInstance();
    private CardsController cardsController = CardsController.getInstance();

    // Views
    private SwipeRefreshLayout srl;
    private SwipeListView slv;
    private ImageButton ibFab;
    private LinearLayout toolbarWrapper;

    // Model
    private LymbosListAdapter lymbosAdapter;

    private Lymbo recentLymbo = null;

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

        REFRESH_DELAY = Integer.parseInt(Configuration.getProperty(this, EProperty.REFRESH_DELAY_LYMBOS));
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

        ibFab = (ImageButton) findViewById(R.id.fab);
        ibFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddStackDialogFragment addStackDialogFragment = new AddStackDialogFragment();
                Bundle b = new Bundle();
                b.putString("type", EDialogType.ADD_STACK.toString());
                b.putString("title", getResources().getString(R.string.add_stack));

                addStackDialogFragment.setArguments(b);
                addStackDialogFragment.show(getFragmentManager(), "okay");
            }
        });

        updateSwipeRefreshProgressBarTop(srl);
        registerHideableHeaderView(toolbarWrapper);
        registerHideableFooterView(ibFab);
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

    @Override
    public void onMessageClick(Parcelable token) {
        cardsController.restore(recentLymbo);
        lymbosAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    // --------------------
    // Methods - Callbacks
    // --------------------

    @Override
    public void onAddStack(String title, String subtitle, String author) {
        Lymbo lymbo = lymbosController.getEmptyLymbo(title, subtitle, author);

        lymbosController.addStack(lymbo);
        lymbosAdapter.add(lymbo);
        lymbosAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_lymbos;
    }

    /**
     * Stashes a lymbo
     *
     * @param lymbo lymbo to be stashed
     */
    public void stash(Lymbo lymbo) {
        slv.invalidateViews();

        recentLymbo = lymbo;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.stack_stashed)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }
}