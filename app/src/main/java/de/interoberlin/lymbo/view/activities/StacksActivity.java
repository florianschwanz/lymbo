package de.interoberlin.lymbo.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.webservice.translate.ELanguage;
import de.interoberlin.lymbo.model.webservice.web.LymboWebDownloadTask;
import de.interoberlin.lymbo.model.webservice.web.LymboWebUploadTask;
import de.interoberlin.lymbo.util.TagUtil;
import de.interoberlin.lymbo.view.adapters.StacksListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.ConfirmRefreshDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.DownloadDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.FilterStacksDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.StackDialogFragment;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class StacksActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, ConfirmRefreshDialogFragment.OnCompleteListener, StackDialogFragment.OnCompleteListener, FilterStacksDialogFragment.OnCompleteListener, LymboWebUploadTask.OnCompleteListener, LymboWebDownloadTask.OnCompleteListener, DownloadDialogFragment.OnCompleteListener, SnackBar.OnMessageClickListener {
    // Controllers
    private StacksController stacksController;

    // Model
    private StacksListAdapter stacksAdapter;

    private Stack recentStack = null;
    private int recentStackPos = -1;
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
            stacksController = StacksController.getInstance(this);
            stacksController.setTagsSelected(stacksController.getTagsAll());

            REFRESH_DELAY = getResources().getInteger(R.integer.refresh_delay_lymbos);
            VIBRATION_DURATION = getResources().getInteger(R.integer.vibration_duration);

            if (stacksController.getStacks().isEmpty()) {
                final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
                srl.post(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(true);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new LoadLymbosTask().execute();
                    }
                }, REFRESH_DELAY);
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
            stacksAdapter = new StacksListAdapter(this, this, R.layout.stack, stacksController.getStacks());

            // Load layout
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

            slv.setAdapter(stacksAdapter);
            slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

            ibFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> tagsAll = TagUtil.getDistinctValues(stacksController.getTagsAll());

                    StackDialogFragment dialog = new StackDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.add_stack));

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StacksActivity.this);
                    Resources res = getResources();
                    String accessToken = prefs.getString(res.getString(R.string.pref_lymbo_web_access_item_access_token), null);
                    if (accessToken != null)
                        bundle.putString(getResources().getString(R.string.bundle_author), prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null));

                    bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), StackDialogFragment.TAG);
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
        getMenuInflater().inflate(R.menu.activity_stacks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_label: {
                selectTags();
                break;
            }
            case R.id.menu_stash: {
                Intent i = new Intent(StacksActivity.this, StacksStashActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_download: {
                download();
                break;
            }
            case R.id.menu_log: {
                Intent i = new Intent(StacksActivity.this, LogActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_about: {
                Intent i = new Intent(StacksActivity.this, AboutActivity.class);
                Bundle b = new Bundle();
                b.putString("flavor", "interoberlin");
                i.putExtras(b);
                startActivity(i);
                break;
            }
            case R.id.menu_settings: {
                Intent i = new Intent(StacksActivity.this, SettingsActivity.class);
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
        ConfirmRefreshDialogFragment dialog = new ConfirmRefreshDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.scan_for_lymbo_files));
        bundle.putString(getResources().getString(R.string.bundle_message), getResources().getString(R.string.scan_for_lymbo_files_question));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), ConfirmRefreshDialogFragment.TAG);
    }

    @Override
    public void onConfirmRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ScanLoadLymbosTask().execute();
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
            case EVENT_STASH: {
                stacksController.restore(recentStack);
                break;
            }
        }

        updateListView();
    }

    @Override
    public void onAddStack(String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags) {
        Stack stack = stacksController.getEmptyStack(title, subtitle, author, languageFrom, languageTo, tags);

        if (!new File(stack.getFile()).exists()) {
            stacksController.addStack(stack);
            stacksController.addTagsSelected(tags);
            stacksAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, getResources().getString(R.string.lymbo_with_same_name_already_exists), Toast.LENGTH_SHORT).show();
        }

        updateListView();
    }

    @Override
    public void onEditStack(String uuid, String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags) {
        stacksController.updateStack(uuid, title, subtitle, author, languageFrom, languageTo, tags);
        stacksController.addTagsSelected(tags);
        stacksAdapter.notifyDataSetChanged();

        updateListView();
    }

    @Override
    public void onTagsSelected(List<Tag> tagsSelected) {
        stacksController.setTagsSelected(tagsSelected);

        snack(this, R.string.tag_selected);
        updateListView();
    }

    @Override
    public void onLymboUploaded(String response) {
        snack(this, R.string.uploaded_lymbo);
    }

    @Override
    public void onLymboDownloaded(String response) {
        if (!response.equals("Error")) {
            Stack stack = LymboLoader.getLymboFromString(this, response);
            stacksController.save(stack);

            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            srl.post(new Runnable() {
                @Override
                public void run() {
                    srl.setRefreshing(true);
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new LoadLymbosTask().execute();
                }
            }, REFRESH_DELAY);

            snack(this, R.string.downloaded_lymbo);
        } else {
            snack(this, R.string.error_downloading_lymbo, SnackBar.Style.ALERT);
        }
    }

    @Override
    public void onDownload(String id) {
        StacksController.getInstance(this).download(id);
    }

    // --------------------
    // Methods - Actions
    // --------------------

    /**
     * Stashes a lymbo
     *
     * @param pos   position of the stack
     * @param stack lymbo to be stashed
     */
    public void stash(int pos, Stack stack) {
        recentStack = stack;
        recentStackPos = pos;
        recentEvent = EVENT_STASH;

        snack(this, R.string.stack_stashed, R.string.undo);
        updateListView();
    }

    /**
     * Opens a dialog to select tags
     */
    private void selectTags() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

        ArrayList<String> tagsAll = TagUtil.getDistinctValues(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = TagUtil.getDistinctValues(stacksController.getTagsSelected());

        FilterStacksDialogFragment dialog = new FilterStacksDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), FilterStacksDialogFragment.TAG);
    }

    private void download() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

        DownloadDialogFragment dialog = new DownloadDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.download));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), DownloadDialogFragment.TAG);
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

        stacksAdapter.filter();
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
            stacksController.load();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

            srl.setRefreshing(false);
            snack(StacksActivity.this, R.string.lymbos_loaded);
            updateListView();
        }
    }

    public class ScanLoadLymbosTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            stacksController.scan();
            stacksController.load();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

            srl.setRefreshing(false);
            snack(StacksActivity.this, R.string.lymbos_loaded);
            updateListView();
        }
    }
}