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

import com.fortysevendeg.swipelistview.SwipeListView;
import com.github.mrengineer13.snackbar.SnackBar;

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

    // Model
    private LymbosListAdapter lymbosAdapter;

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

        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        srl.setOnRefreshListener(this);
        srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        slv = (SwipeListView) findViewById(R.id.slv);
        slv.setAdapter(lymbosAdapter);
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
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