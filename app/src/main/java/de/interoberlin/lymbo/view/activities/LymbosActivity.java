package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.fortysevendeg.swipelistview.SwipeListView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.view.adapters.LymbosListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;

public class LymbosActivity extends BaseActivity implements DisplayDialogFragment.OnCompleteListener {
    // Controllers
    private LymbosController lymbosController = LymbosController.getInstance();
    private CardsController cardsController = CardsController.getInstance();

    // Context and Activity
    private static Context context;
    private static Activity activity;

    // Views
    private SwipeListView slv;

    // Model
    private LymbosListAdapter lymbosAdapter;

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

        // Get activity and context for further use
        activity = this;
        context = this;
    }

    public void onResume() {
        super.onResume();
        lymbosAdapter = new LymbosListAdapter(activity, context, R.layout.stack, lymbosController.getLymbos());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

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

    // --------------------
    // Methods - Callbacks
    // --------------------

    @Override
    public void onHintDialogComplete() {

    }

    @Override
    public void onDiscardCardDialogComplete() {

    }

    @Override
    public void onStashStackDialogComplete() {
        cardsController.stash();
        lymbosAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    @Override
    public void onRestoreStackDialogComplete() {
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