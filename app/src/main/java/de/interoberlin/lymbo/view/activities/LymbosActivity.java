package de.interoberlin.lymbo.view.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.translate.Language;
import de.interoberlin.lymbo.view.adapters.LymbosListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.FilterStacksDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.StackDialogFragment;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class LymbosActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, StackDialogFragment.OnCompleteListener, FilterStacksDialogFragment.OnCompleteListener, SnackBar.OnMessageClickListener {
    // Controllers
    private LymbosController lymbosController;

    // Model
    private LymbosListAdapter lymbosAdapter;

    private Lymbo recentLymbo = null;
    // private int recentCardPos = -1;
    private int recentEvent = -1;

    private static final int EVENT_STASH = 2;
    private static int REFRESH_DELAY;
    private static int VIBRATION_DURATION;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            lymbosController = LymbosController.getInstance(this);
            lymbosController.setTagsSelected(lymbosController.getTagsAll());

            setActionBarIcon(R.drawable.ic_ab_drawer);
            setDisplayHomeAsUpEnabled(true);

            REFRESH_DELAY = getResources().getInteger(R.integer.refresh_delay_lymbos);
            VIBRATION_DURATION = getResources().getInteger(R.integer.vibration_duration);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void onResume() {
        try {
            super.onResume();
            lymbosAdapter = new LymbosListAdapter(this, this, R.layout.stack, lymbosController.getLymbos());

            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
            final LinearLayout toolbarWrapper = (LinearLayout) findViewById(R.id.toolbar_wrapper);
            final TextView toolbarTitleView = (TextView) findViewById(R.id.toolbar_title);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);
            final ImageButton ibFab = (ImageButton) findViewById(R.id.fab);

            toolbarTitleView.setText(R.string.lymbos);

            drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            srl.setOnRefreshListener(this);
            srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

            slv.setAdapter(lymbosAdapter);
            slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

            ibFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> tagsAll = Tag.getNames(lymbosController.getTagsAll());

                    StackDialogFragment dialog = new StackDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "okay");
                }
            });

            updateSwipeRefreshProgressBarTop(srl);
            registerHideableHeaderView(toolbarWrapper);
            registerHideableFooterView(ibFab);
            enableActionBarAutoHide(slv);

            updateListView();
        } catch (Exception e) {
            handleException(e);
        }
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
            case R.id.menu_label: {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

                ArrayList<String> tagsAll = Tag.getNames(lymbosController.getTagsAll());
                ArrayList<String> tagsSelected = Tag.getNames(lymbosController.getTagsSelected());

                FilterStacksDialogFragment dialog = new FilterStacksDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
                bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "okay");
                break;
            }
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
            case R.id.menu_settings: {
                Intent i = new Intent(LymbosActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
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
                new LoadLymbosTask().execute();
            }
        }, REFRESH_DELAY);
    }

    @Override
    public void onMessageClick(Parcelable token) {
        switch (recentEvent) {
            case EVENT_STASH: {
                lymbosController.restore(recentLymbo);
                break;
            }
        }

        updateListView();
    }

    @Override
    public void onAddStack(String title, String subtitle, String author, Language languageFrom, Language languageTo, List<Tag> tags) {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

        Lymbo lymbo = lymbosController.getEmptyLymbo(title, subtitle, author, languageFrom, languageTo, tags);

        if (!new File(lymbo.getPath()).exists()) {
            lymbosController.addStack(lymbo);
            lymbosController.addTagsSelected(tags);
            lymbosAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, getResources().getString(R.string.lymbo_with_same_name_already_exists), Toast.LENGTH_SHORT).show();
        }
        slv.invalidateViews();
    }

    @Override
    public void onEditStack(String uuid, String title, String subtitle, String author, Language languageFrom, Language languageTo, List<Tag> tags) {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

        lymbosController.updateStack(uuid, title, subtitle, author, languageFrom, languageTo, tags);
        lymbosController.addTagsSelected(tags);
        lymbosAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    @Override
    public void onTagsSelected(List<Tag> tagsSelected) {
        lymbosController.setTagsSelected(tagsSelected);

        snackTagSelected();
        updateListView();
    }

    // --------------------
    // Methods - Actions
    // --------------------

    /**
     * Stashes a lymbo
     *
     * @param lymbo lymbo to be stashed
     */
    public void stash(Lymbo lymbo) {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);
        slv.invalidateViews();

        recentLymbo = lymbo;
        recentEvent = EVENT_STASH;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.stack_stashed)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that lymbos have been loaded
     */
    public void snackLymbosLoaded() {
        new SnackBar.Builder(this)
                .withMessageId(R.string.lymbos_loaded)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that tags have been slected
     */
    public void snackTagSelected() {
        new SnackBar.Builder(this)
                .withMessageId(R.string.tag_selected)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_lymbos;
    }

    /**
     * Updates the list view
     */
    private void updateListView() {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

        lymbosAdapter.filter();
        slv.closeOpenedItems();
        slv.invalidateViews();
    }

    // --------------------
    // Inner classes
    // --------------------

    public class LoadLymbosTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            lymbosController.scan();
            lymbosController.load();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

            updateListView();
            srl.setRefreshing(false);
            snackLymbosLoaded();
        }

    }
}