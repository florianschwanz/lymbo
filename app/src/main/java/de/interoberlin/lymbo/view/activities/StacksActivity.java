package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
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
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.ELanguage;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.share.MailSender;
import de.interoberlin.lymbo.model.webservice.AccessControlItem;
import de.interoberlin.lymbo.model.webservice.web.LymboWebAccessControlItemTask;
import de.interoberlin.lymbo.model.webservice.web.LymboWebDownloadTask;
import de.interoberlin.lymbo.model.webservice.web.LymboWebUploadTask;
import de.interoberlin.lymbo.view.adapters.StacksListAdapter;
import de.interoberlin.lymbo.view.dialogs.ConfirmRefreshDialog;
import de.interoberlin.lymbo.view.dialogs.DownloadDialog;
import de.interoberlin.lymbo.view.dialogs.FilterStacksDialog;
import de.interoberlin.lymbo.view.dialogs.StackDialog;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.SwipeListView;

public class StacksActivity extends SwipeRefreshBaseActivity implements
    // <editor-fold defaultstate="expanded" desc="Interfaces">
        SwipeRefreshLayout.OnRefreshListener,
        StacksListAdapter.OnCompleteListener,
        SnackBar.OnMessageClickListener,
        ConfirmRefreshDialog.OnCompleteListener,
        StackDialog.OnCompleteListener,
        FilterStacksDialog.OnCompleteListener,
        DownloadDialog.OnCompleteListener,
        LymboWebUploadTask.OnCompleteListener,
        LymboWebDownloadTask.OnCompleteListener {
    // </editor-fold>

    // <editor-fold defaultstate="expanded" desc="Members">

    // View
    private StacksListAdapter stacksAdapter;
    @BindView(R.id.dl) DrawerLayout drawer;
    @BindView(R.id.toolbar_wrapper) LinearLayout toolbarWrapper;
    @BindView(R.id.toolbar_title) TextView toolbarTitleView;
    @BindView(R.id.swipe_container) SwipeRefreshLayout srl;
    @BindView(R.id.slv) SwipeListView slv;
    @BindView(R.id.fab) ImageButton ibFab;

    // Controller
    private StacksController stacksController;

    private Stack recentStack = null;
    @SuppressWarnings("ununsed")
    private int recentStackPos = -1;
    private int recentEvent = -1;

    // Properties
    private static final int EVENT_STASH = 2;
    private static int REFRESH_DELAY;
    private static int VIBRATION_DURATION;

    // </editor-fold>

    // --------------------
    // Methods - Lifecycle
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Lifecycle">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            stacksController = StacksController.getInstance();
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

            setActionBarIcon(R.drawable.ic_menu_white_24dp);
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
            stacksAdapter = new StacksListAdapter(this, this, R.layout.stack, stacksController.getStacks());

            toolbarTitleView.setText(R.string.lymbos);

            drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            srl.setOnRefreshListener(this);
            srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

            slv.setAdapter(stacksAdapter);
            slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

            ibFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> tagsAll = Tag.getValues(stacksController.getTagsAll());

                    StackDialog dialog = new StackDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.add_stack));

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StacksActivity.this);
                    Resources res = getResources();
                    String accessToken = prefs.getString(res.getString(R.string.pref_lymbo_web_access_item_access_token), null);
                    if (accessToken != null)
                        bundle.putString(getResources().getString(R.string.bundle_author), prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null));

                    bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), StackDialog.TAG);
                }
            });

            updateSwipeRefreshProgressBarTop(srl);
            registerHideableHeaderView(toolbarWrapper);
            registerHideableFooterView(ibFab);
            enableActionBarAutoHide(slv);

            updateView();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_stacks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter: {
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

    // </editor-fold>

    // --------------------
    // Methods - Callbacks
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Callbacks">

    // <editor-fold defaultstate="collapsed" desc="Callbacks SwipeRefreshLayout">
    @Override
    public void onRefresh() {
        ConfirmRefreshDialog dialog = new ConfirmRefreshDialog();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.scan_for_lymbo_files));
        bundle.putString(getResources().getString(R.string.bundle_message), getResources().getString(R.string.scan_for_lymbo_files_question));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), ConfirmRefreshDialog.TAG);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks StacksListAdapter">
    @Override
    public void onClickEdit(Stack stack) {
        String uuid = stack.getId();
        String title = stack.getTitle();
        String subtitle = stack.getSubtitle();
        String author = stack.getAuthor();
        String languageFrom = null;
        String languageTo = null;
        ArrayList<String> tagsAll = Tag.getValues(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = Tag.getValues(stack.getTags());

        if (stack.getLanguage() != null && stack.getLanguage().getFrom() != null && stack.getLanguage().getTo() != null) {
            languageFrom = stack.getLanguage().getFrom();
            languageTo = stack.getLanguage().getTo();
        }

        vibrate();

        StackDialog dialog = new StackDialog();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.edit_stack));
        bundle.putString(getResources().getString(R.string.bundle_lymbo_uuid), uuid);
        bundle.putString(getResources().getString(R.string.bundle_title), title);
        bundle.putString(getResources().getString(R.string.bundle_subtitle), subtitle);
        bundle.putString(getResources().getString(R.string.bundle_author), author);
        bundle.putString(getResources().getString(R.string.bundle_language_from), languageFrom);
        bundle.putString(getResources().getString(R.string.bundle_language_to), languageTo);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), StackDialog.TAG);
    }

    @Override
    public void onClickStash(int position, Stack stack) {
        stacksController.stash(this, stack);

        recentStack = stack;
        recentStackPos = position;
        recentEvent = EVENT_STASH;

        snack(this, R.string.stack_stashed, R.string.undo);
        updateView();
    }

    @Override
    public void onClickSelectTags() {
        vibrate();
        selectTags();
    }

    @Override
    public void onClickSend(Stack stack) {
        MailSender.sendLymbo(this, this, stack);
    }

    @Override
    public void onClickUpload(Stack stack) {
        Resources res = getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String username = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);
        String password = prefs.getString(res.getString(R.string.pref_lymbo_web_password), null);
        String clientId = res.getString(R.string.pref_lymbo_web_client_id);
        String clientSecret = prefs.getString(res.getString(R.string.pref_lymbo_web_api_secret), null);

        String id = stack.getId();
        String author = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);
        String content = stack.toString();

        try {
            AccessControlItem accessControlItem = new LymboWebAccessControlItemTask().execute(username, password, clientId, clientSecret).get();

            if (accessControlItem != null && accessControlItem.getAccess_token() != null) {
                new LymboWebUploadTask(this).execute(accessControlItem.getAccess_token(), id, author, content).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks Snackbar">
    @Override
    public void onMessageClick(Parcelable token) {
        switch (recentEvent) {
            case EVENT_STASH: {
                stacksController.restore(this, recentStack);
                break;
            }
        }

       updateView();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks RefreshDialog">
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks StackDialog">
    @Override
    public void onAddStack(String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags) {
        Stack stack = stacksController.getEmptyStack(title, subtitle, author, languageFrom, languageTo, tags);

        if (!new File(stack.getFile()).exists()) {
            stacksController.addStack(this, stack);
            stacksController.addTagsSelected(tags);
            stacksAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, getResources().getString(R.string.lymbo_with_same_name_already_exists), Toast.LENGTH_SHORT).show();
        }

        updateView();
    }

    @Override
    public void onEditStack(String uuid, String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags) {
        stacksController.updateStack(this, uuid, title, subtitle, author, languageFrom, languageTo, tags);
        stacksController.addTagsSelected(tags);
        stacksAdapter.notifyDataSetChanged();

        updateView();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks FilterCardsDialog">
    @Override
    public void onTagsSelected(List<Tag> tagsSelected) {
        stacksController.setTagsSelected(tagsSelected);

        snack(this, R.string.tag_selected);
        updateView();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks DownloadDialog">
    @Override
    public void onDownload(String id) {
        stacksController.download(this, this, id);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks LymboWebUploadTask">
    @Override
    public void onLymboUploaded(String response) {
        snack(this, R.string.uploaded_lymbo);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Callbacks LymboWebDownloadTask">
    @Override
    public void onLymboDownloaded(String response) {
        if (!response.equals("Error")) {
            Stack stack = LymboLoader.getLymboFromString(this, response, false);
            stacksController.save(this, stack);

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
    // </editor-fold>

    // </editor-fold>

    // --------------------
    // Methods - Actions
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Actions">

    /**
     * Opens a dialog to select tags
     */
    private void selectTags() {
        vibrate();

        ArrayList<String> tagsAll = Tag.getValues(stacksController.getTagsAll());
        ArrayList<String> tagsSelected = Tag.getValues(stacksController.getTagsSelected());

        FilterStacksDialog dialog = new FilterStacksDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), FilterStacksDialog.TAG);
    }

    private void download() {
        vibrate();

        DownloadDialog dialog = new DownloadDialog();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.download));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), DownloadDialog.TAG);
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Methods">

    private void vibrate() {
        vibrate(VIBRATION_DURATION);
    }

    private void vibrate(int VIBRATION_DURATION) {
        ((Vibrator) getSystemService(Activity.VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_stacks;
    }

    /**
     * Updates the list view
     */
    private void updateView() {
        final SwipeListView slv = (SwipeListView) findViewById(R.id.slv);

        stacksAdapter.filter();
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
            stacksController.load(StacksActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

            srl.setRefreshing(false);
            snack(StacksActivity.this, R.string.lymbos_loaded);
            updateView();
        }
    }

    // </editor-fold>

    // --------------------
    // Tasks
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Tasks">

    public class ScanLoadLymbosTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            stacksController.scan(StacksActivity.this);
            stacksController.load(StacksActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

            srl.setRefreshing(false);
            snack(StacksActivity.this, R.string.lymbos_loaded);
            updateView();
        }
    }
    // </editor-fold>
}