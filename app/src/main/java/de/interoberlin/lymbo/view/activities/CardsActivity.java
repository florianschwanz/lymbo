package de.interoberlin.lymbo.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.io.File;
import java.util.Collections;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.persistence.LymboLoader;
import de.interoberlin.lymbo.view.adapters.CardsListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.CheckboxDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;
import de.interoberlin.lymbo.view.dialogfragments.SimpleCardDialogFragment;
import de.interoberlin.mate.lib.util.Toaster;

public class CardsActivity extends BaseActivity implements SimpleCardDialogFragment.OnCompleteListener ,DisplayDialogFragment.OnCompleteListener, CheckboxDialogFragment.OnLabelSelectedListener {
    // Controllers
    CardsController cardsController = CardsController.getInstance();

    // Context and Activity
    private static Context context;
    // private static Activity activity;

    // Views
    private SwipeListView slv;
    private ImageButton ibFab;

    // Model
    private List<Card> cards;
    private CardsListAdapter cardsAdapter;

    private final String BUNDLE_LYMBO_PATH = "lymbo_path";
    private final String BUNDLE_ASSET = "asset";
    private final String BUNDLE_SCROLL_POS = "scroll_pos";

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
                    l = LymboLoader.getLymboFromAsset(getApplicationContext(), savedInstanceState.getString(BUNDLE_LYMBO_PATH));
                } else {
                    l = LymboLoader.getLymboFromFile(new File(savedInstanceState.getString(BUNDLE_LYMBO_PATH)));
                }
            }

            cardsController.setLymbo(l);
            cardsController.init();
        }

        if (cardsController.getLymbo() == null) {
            finish();
        }

        setActionBarIcon(R.drawable.ic_ab_drawer);

        // Register on toaster
        Toaster.register(this, context);

        // Get activity and context for further use
        // activity = this;
        context = getApplicationContext();
    }

    public void onResume() {
        super.onResume();
        cards = cardsController.getCards();
        cardsAdapter = new CardsListAdapter(this, this, R.layout.card, cards);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // Get list view and add adapter
        slv = (SwipeListView) findViewById(R.id.slv);
        slv.setAdapter(cardsAdapter);
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
        slv.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
        slv.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
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
                for (int position : reverseSortedPositions) {
                    cards.get(position).setDiscarded(true);
                }
                cardsAdapter.notifyDataSetChanged();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                Collections.shuffle(cards);
                cardsAdapter.notifyDataSetChanged();
                break;
            }
            case R.id.menu_refresh: {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
                for (Card c : cards) {
                    c.setDiscarded(false);
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
        cardsController.save();
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

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_cards;
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