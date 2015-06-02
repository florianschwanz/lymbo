package de.interoberlin.lymbo.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.github.mrengineer13.snackbar.SnackBar;

import java.io.File;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.persistence.LymboLoader;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.view.adapters.CardsListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.CheckboxDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;
import de.interoberlin.lymbo.view.dialogfragments.SimpleCardDialogFragment;

public class CardsActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, SimpleCardDialogFragment.OnCompleteListener, DisplayDialogFragment.OnCompleteListener, CheckboxDialogFragment.OnLabelSelectedListener, SnackBar.OnMessageClickListener {
    // Controllers
    CardsController cardsController = CardsController.getInstance();

    // Context and Activity
    private static Context context;
    // private static Activity activity;

    // Views
    private SwipeRefreshLayout srl;
    private SwipeListView slv;
    private ImageButton ibFab;
    private LinearLayout toolbarWrapper;

    // Model
    private Lymbo lymbo;
    private CardsListAdapter cardsAdapter;

    private final String BUNDLE_LYMBO_PATH = "lymbo_path";
    private final String BUNDLE_ASSET = "asset";

    private int previousCardId = -1;

    private static int REFRESH_DELAY;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore instance state
        if (savedInstanceState != null) {
            // Restore cards
            Lymbo l = null;
            if (savedInstanceState.getString(BUNDLE_LYMBO_PATH) != null) {
                if (savedInstanceState.getBoolean(BUNDLE_ASSET)) {
                    l = LymboLoader.getLymboFromAsset(context, savedInstanceState.getString(BUNDLE_LYMBO_PATH), false);
                } else {
                    l = LymboLoader.getLymboFromFile(new File(savedInstanceState.getString(BUNDLE_LYMBO_PATH)), false);
                }
            }

            cardsController.setLymbo(l);
            cardsController.init();
        }

        if (cardsController.getLymbo() == null) {
            finish();
        }

        setActionBarIcon(R.drawable.ic_ab_drawer);
        setDisplayHomeAsUpEnabled(true);

        REFRESH_DELAY = Integer.parseInt(Configuration.getProperty(this, EProperty.REFRESH_DELAY));
    }

    public void onResume() {
        super.onResume();
        lymbo = cardsController.getLymbo();
        cardsAdapter = new CardsListAdapter(this, this, R.layout.card, cardsController.getCards());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        toolbarWrapper = (LinearLayout) findViewById(R.id.toolbar_wrapper);

        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        srl.setOnRefreshListener(this);
        srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        slv = (SwipeListView) findViewById(R.id.slv);
        slv.setAdapter(cardsAdapter);
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
        slv.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        slv.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        slv.setSwipeOpenOnLongPress(false);

        slv.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
            }

            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) {
            }

            @Override
            public void onClickBackView(int position) {
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            }
        });

        ibFab = (ImageButton) findViewById(R.id.fab);
        ibFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleCardDialogFragment simpleCardDialogFragment = new SimpleCardDialogFragment();
                Bundle b = new Bundle();
                b.putString("type", EDialogType.ADD_SIMPLE_CARD.toString());
                b.putString("title", getResources().getString(R.string.add_card));

                simpleCardDialogFragment.setArguments(b);
                simpleCardDialogFragment.show(getFragmentManager(), "okay");
            }
        });

        if (lymbo.isAsset()) {
            ibFab.setVisibility(View.INVISIBLE);
        }

        updateSwipeRefreshProgressBarTop(srl);
        registerHideableHeaderView(toolbarWrapper);
        registerHideableFooterView(ibFab);
        enableActionBarAutoHide(slv);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cardsController.setLymbo(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int VIBRATION_DURATION = 50;

        switch (item.getItemId()) {
            case R.id.menu_shuffle: {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
                cardsController.shuffle();
                cardsAdapter.notifyDataSetChanged();
                break;
            }
            case R.id.menu_refresh: {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
                for (Card c : lymbo.getCards()) {
                    // c.setDismissed(false);
                }
                cardsAdapter.notifyDataSetChanged();
                break;
            }
            case R.id.menu_label: {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
                CheckboxDialogFragment checkboxDialogFragment = new CheckboxDialogFragment();

                Bundle b = new Bundle();
                b.putCharSequence("type", EDialogType.SELECT_LABEL.toString());

                checkboxDialogFragment.setArguments(b);
                checkboxDialogFragment.show(getFragmentManager(), "okay");
                break;
            }
        }

        return true;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // srl.setRefreshing(false);
                // slv.invalidateViews();
            }
        }, REFRESH_DELAY);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            slv.smoothScrollToPosition(getFirst() + 2);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            slv.smoothScrollToPosition(getFirst() - 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(BUNDLE_LYMBO_PATH, cardsController.getLymbo().getPath());
        savedInstanceState.putBoolean(BUNDLE_ASSET, cardsController.getLymbo().isAsset());

        super.onSaveInstanceState(savedInstanceState);
    }

    // --------------------
    // Methods
    // --------------------

    /**
     * Puts a card with a given index to the end
     *
     * @param pos index of the card to be moved
     */
    public void putToEnd(int pos) {
        slv.invalidateViews();
        previousCardId = pos;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.put_card_to_end)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    private int getFirst() {
        int first = slv.getFirstVisiblePosition();
        if (slv.getChildAt(0).getTop() < 0)
            first++;

        return first;
    }

    // --------------------
    // Methods - Callbacks
    // --------------------

    @Override
    public void onAddSimpleCard(String frontText, String backText) {
        cardsController.addSimpleCard(frontText, backText);
        cardsAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    @Override
    public void onHintDialogComplete() {

    }

    @Override
    public void onDiscardCardDialogComplete() {

    }

    @Override
    public void onLabelSelected() {
        cardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageClick(Parcelable parcelable) {
        cardsController.putLastItemToPos(previousCardId);
        cardsAdapter.notifyDataSetChanged();
        slv.invalidateViews();
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_cards;
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