package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.fortysevendeg.swipelistview.SwipeListView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.view.adapters.LymbosListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;

public class LymbosActivity extends BaseActivity implements DisplayDialogFragment.OnCompleteListener {
    // Controllers
    LymbosController lymbosController = LymbosController.getInstance();

    // Context and Activity
    private static Context c;
    private static Activity a;

    // Views
    private DrawerLayout drawer;
    private SwipeListView slv;
    private RecyclerView.LayoutManager lm;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_ab_drawer);
        setDisplayHomeAsUpEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // Get activity and context for further use
        a = this;
        c = getApplicationContext();

        SwipeListView slv = (SwipeListView) findViewById(R.id.slv);
        slv.setAdapter(new LymbosListAdapter(this, R.layout.stack, lymbosController.getLymbos()));
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
    }

    public void onResume() {
        super.onResume();
        clear();
        draw();
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
    public void onDiscardStackDialogComplete() {

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

    private static void clear() {
        // rv.removeAllViews();
    }

    public void draw() {
    }

    public void uiRefresh() {
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear();
                draw();
            }
        });
    }



    /*
    @Override
    public void onCreateStackDialogComplete(String input) {
        StackController.createEmptyStack(input);
        StackController.findLymboFiles();

        clear();
        draw();
    }
    */

    /*
    @Override
    public void onChangeStackDialogComplete(final String input, final String file) {
        StackController.renameStack(input, file);
        StackController.findLymboFiles();

        clear();
        draw();
    }
    */

    /*
    @Override
    public void onDiscardStackDialogComplete() {
        StackController.removeStack(Properties.getCurrentFileString());
        StackController.findLymboFiles();

        clear();
        draw();
    }
    */

    /*
    @Override
    public void onDiscardCardDialogComplete() {
        // Not relevant
    }

    /*
    @Override
    public void onDownloadBlob(String input) {
        StackController.download(a, input);
        StackController.findLymboFiles();

        clear();
        draw();
    }
    */

}