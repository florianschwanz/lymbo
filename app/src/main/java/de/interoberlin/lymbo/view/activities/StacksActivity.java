package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.LymboController;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.mate.lib.model.Log;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;

public class StacksActivity extends BaseActivity {
    // Controllers
    StacksController stacksController = StacksController.getInstance();
    LymboController lymboController = LymboController.getInstance();

    // Context and Activity
    private static Context c;
    private static Activity a;

    // Views
    private static RecyclerView rv;
    private RecyclerView.Adapter ca;
    private RecyclerView.LayoutManager lm;

    private Collection<File> lymboFiles = stacksController.getLymboFiles();

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stacks);

/*
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        stackController.setDisplayHeight(displaymetrics.heightPixels);
        stackController.setDisplayWidth(displaymetrics.widthPixels);
*/
        // Get activity and context for further use
        a = this;
        c = getApplicationContext();

        // Get Controls by ID
        rv = (RecyclerView) findViewById(R.id.rv);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv.setHasFixedSize(true);

        // use a linear layout manager
        lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);

        List<CardView> cards = new ArrayList<CardView>();

        cards.add(new CardView(c));

        ca = new CardAdapter(cards);
        rv.setAdapter(ca);

        /*
        if (lymboFiles == null || lymboFiles.isEmpty()) {
            lymboController.findLymboFiles();
            clear();
            draw();
        }
        */
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
        getMenuInflater().inflate(R.menu.activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log: {
                Intent i = new Intent(StacksActivity.this, LogActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_about: {
                Intent i = new Intent(StacksActivity.this, AboutActivity.class);
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
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_stacks;
    }

    public static Collection<File> findFiles(String pattern) {
        Log.trace("StackActivity.findFiles()");
        return FileUtils.listFiles(Environment.getExternalStorageDirectory(), new RegexFileFilter(".*" + pattern), TrueFileFilter.TRUE);
    }

    private static void clear() {
        rv.removeAllViews();
    }

    public void draw() {
        // Get list of lymbo files
        lymboFiles = stacksController.getLymboFiles();

        if (lymboController.checkStorage()) {
            a.setTitle(a.getResources().getString(R.string.app_name));

        }
    }

    public static void uiToast(final String message) {
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
            }
        });
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
    public void onHintDialogComplete() {
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