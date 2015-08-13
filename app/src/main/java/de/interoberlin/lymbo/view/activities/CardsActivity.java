package de.interoberlin.lymbo.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;

import java.io.File;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.lymbo.view.adapters.CardsListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.AddCardDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.CopyCardDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.DisplayHintDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EditCardDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EditNoteDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.MoveCardDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.SelectTagsDialogFragment;
import de.interoberlin.mate.lib.view.AboutActivity;
import de.interoberlin.mate.lib.view.LogActivity;
import de.interoberlin.swipelistview.view.BaseSwipeListViewListener;
import de.interoberlin.swipelistview.view.SwipeListView;

public class CardsActivity extends SwipeRefreshBaseActivity implements SwipeRefreshLayout.OnRefreshListener, AddCardDialogFragment.OnCompleteListener, EditCardDialogFragment.OnCompleteListener, DisplayHintDialogFragment.OnCompleteListener, SelectTagsDialogFragment.OnTagsSelectedListener, EditNoteDialogFragment.OnCompleteListener, SnackBar.OnMessageClickListener, CopyCardDialogFragment.OnCopyCardListener, MoveCardDialogFragment.OnMoveCardListener {
    // Controllers
    private CardsController cardsController;

    // Views
    private SwipeRefreshLayout srl;
    private SwipeListView slv;
    private ImageButton ibFab;
    private LinearLayout toolbarWrapper;
    private TextView toolbarTextView;
    private RelativeLayout rl;
    private LinearLayout phNoCards;
    private RelativeLayout rlReveal;
    private ImageView ivDiscard;
    private ImageView ivPutToEnd;

    // Model
    private Lymbo lymbo;
    private CardsListAdapter cardsAdapter;

    private Card recentCard = null;
    private int recentCardPos = -1;
    private int recentEvent = -1;

    private static final int EVENT_DISCARD = 0;
    private static final int EVENT_PUT_TO_END = 1;
    private static final int EVENT_STASH = 2;
    private static final int EVENT_GENERATED_IDS = 3;

    // Properties
    private final String BUNDLE_LYMBO_PATH = "lymbo_path";
    private final String BUNDLE_ASSET = "asset";
    private static int REFRESH_DELAY;
    private static int VIBRATION_DURATION;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            cardsController = CardsController.getInstance(this);

            // Restore instance state
            if (savedInstanceState != null) {
                // Restore cards
                Lymbo l = null;
                if (savedInstanceState.getString(BUNDLE_LYMBO_PATH) != null) {
                    if (savedInstanceState.getBoolean(BUNDLE_ASSET)) {
                        l = LymboLoader.getLymboFromAsset(this, savedInstanceState.getString(BUNDLE_LYMBO_PATH), false);
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

            // Properties
            REFRESH_DELAY = Integer.parseInt(Configuration.getProperty(this, EProperty.REFRESH_DELAY_CARDS));
            VIBRATION_DURATION = Integer.parseInt(Configuration.getProperty(this, EProperty.VIBRATION_DURATION));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void onResume() {
        try {
            super.onResume();
            lymbo = cardsController.getLymbo();
            cardsAdapter = new CardsListAdapter(this, this, R.layout.card, cardsController.getCards());

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
            drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            toolbarWrapper = (LinearLayout) findViewById(R.id.toolbar_wrapper);
            toolbarTextView = (TextView) findViewById(R.id.toolbar_text);

            rl = (RelativeLayout) findViewById(R.id.rl);

            srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            srl.setOnRefreshListener(this);
            srl.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

            slv = (SwipeListView) findViewById(R.id.slv);
            slv.setAdapter(cardsAdapter);
            slv.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
            slv.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
            slv.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
            slv.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
            slv.setFastScrollEnabled(true);

            slv.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onOpened(int position, boolean toRight) {
                    srl.setEnabled(true);

                    Card card = cardsAdapter.getFilteredItems().get(position);

                    if (toRight) {
                        cardsController.discard(card);
                        snackDiscard(position, card);
                    } else {
                        cardsController.putToEnd(card);
                        snackPutToEnd(position, card);
                    }

                    updateListView();
                }

                @Override
                public void onClosed(int position, boolean fromRight) {
                    srl.setEnabled(true);
                }

                @Override
                public void onListChanged() {
                }

                @Override
                public void onMove(int position, float x) {
                    View v = slv.getChildAt(position);

                    if (v != null) {
                        rlReveal = (RelativeLayout) v.findViewById(R.id.reveal);
                        ivDiscard = (ImageView) v.findViewById(R.id.ivDiscard);
                        ivPutToEnd = (ImageView) v.findViewById(R.id.ivPutToEnd);

                        if (x > 0) {
                            rlReveal.setBackgroundColor(getResources().getColor(R.color.action_discard));
                            ivDiscard.setVisibility(View.VISIBLE);
                            ivPutToEnd.setVisibility(View.INVISIBLE);
                        } else {
                            rlReveal.setBackgroundColor(getResources().getColor(R.color.action_put_to_end));
                            ivDiscard.setVisibility(View.INVISIBLE);
                            ivPutToEnd.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onStartOpen(int position, int action, boolean right) {
                    srl.setEnabled(false);
                }

                @Override
                public void onStartClose(int position, boolean right) {
                    srl.setEnabled(false);
                }

                @Override
                public void onClickFrontView(int position) {
                    Card card = cardsAdapter.getFilteredItems().get(0);
                    Toast.makeText(getBaseContext(), position + " : " + card.toString(), Toast.LENGTH_SHORT).show();
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
                    new AddCardDialogFragment().show(getFragmentManager(), "okay");
                }
            });

            if (lymbo.isAsset()) {
                ibFab.setVisibility(View.INVISIBLE);
            }

            phNoCards = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.placeholder_no_cards, null, false);
            rl.addView(phNoCards);

            updateSwipeRefreshProgressBarTop(srl);
            registerHideableHeaderView(toolbarWrapper);
            registerHideableFooterView(ibFab);
            enableActionBarAutoHide(slv);

            updateListView();
        } catch (
                Exception e
                )

        {
            handleException(e);
        }

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
        switch (item.getItemId()) {
            case R.id.menu_stash: {
                Intent i = new Intent(CardsActivity.this, CardsStashActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_shuffle: {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);

                cardsController.shuffle();

                updateListView();
                break;
            }
            case R.id.menu_label: {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
                new SelectTagsDialogFragment().show(getFragmentManager(), "okay");
                break;
            }
            case R.id.menu_log: {
                Intent i = new Intent(CardsActivity.this, LogActivity.class);
                startActivity(i);
                break;
            }
            case R.id.menu_about: {
                Intent i = new Intent(CardsActivity.this, AboutActivity.class);
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
    // Methods - Callbacks
    // --------------------

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);

                cardsController.reset();

                updateListView();
            }
        }, REFRESH_DELAY);
    }

    @Override
    public void onMessageClick(Parcelable parcelable) {
        switch (recentEvent) {
            case EVENT_STASH: {
                cardsController.restore(recentCardPos, recentCard);
                break;
            }
            case EVENT_DISCARD: {
                cardsController.retain(recentCardPos, recentCard);
                break;
            }
            case EVENT_PUT_TO_END: {
                cardsController.putLastItemToPos(recentCardPos);
                break;
            }
            case EVENT_GENERATED_IDS: {
                lymbo.setContainsGeneratedIds(false);
                break;
            }
        }

        updateListView();
    }

    @Override
    public void onAddSimpleCard(String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        try {
            cardsController.addCard(frontTitleValue, frontTextsValues, backTitleValue, backTextsValues, tags);

            updateListView();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onEditSimpleCard(String uuid, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        cardsController.updateCard(uuid, frontTitleValue, frontTextsValues, backTitleValue, backTextsValues, tags);
        snackEditCard();
        updateListView();
    }

    @Override
    public void onHintDialogComplete() {
    }

    @Override
    public void onTagsSelected() {
        snackTagSelected();
        updateListView();
    }

    @Override
    public void onEditNote(String uuid, String note) {
        cardsController.setNote(this, uuid, note);
        snackEditNote();
        updateListView();
    }

    @Override
    public void onCopyCard(String sourceLymboId, String targetLymboId, String cardUuid, boolean deepCopy) {
        if (sourceLymboId != null && targetLymboId != null && cardUuid != null) {
            cardsController.copyCard(sourceLymboId, targetLymboId, cardUuid, deepCopy);
            snackCopyCard();
            updateListView();
        }
    }

    @Override
    public void onMoveCard(String sourceLymboId, String targetLymboId, String cardUuid) {
        if (sourceLymboId != null && targetLymboId != null && cardUuid != null) {
            cardsController.moveCard(sourceLymboId, targetLymboId, cardUuid);
            snackMoveCard();
            updateListView();
        }
    }

    // --------------------
    // Methods - Actions
    // --------------------

    /**
     * Indicates that a card has been stashed
     *
     * @param pos  position of the card
     * @param card card to be stashed
     */
    public void stash(int pos, Card card) {
        recentCard = card;
        recentCardPos = pos;
        recentEvent = EVENT_STASH;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.stashed_card)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that a card has been discarded
     *
     * @param pos  position of the card
     * @param card card to be discarded
     */
    public void snackDiscard(int pos, Card card) {
        recentCard = card;
        recentCardPos = pos;
        recentEvent = EVENT_DISCARD;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.discard_card)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that a card has been put tho the end
     *
     * @param pos  poistion of the card
     * @param card card to be put to the end
     */
    public void snackPutToEnd(int pos, Card card) {
        recentCard = card;
        recentCardPos = pos - 1;
        recentEvent = EVENT_PUT_TO_END;

        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.put_card_to_end)
                .withActionMessageId(R.string.undo)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that a card has been edited
     */
    public void snackEditCard() {
        new SnackBar.Builder(this)
                .withMessageId(R.string.edited_card)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that tags have been selected
     */
    public void snackTagSelected() {
        new SnackBar.Builder(this)
                .withMessageId(R.string.tag_selected)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that a note has been edited
     */
    public void snackEditNote() {
        updateListView();

        new SnackBar.Builder(this)
                .withMessageId(R.string.note_edited)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that a card has been copies
     */
    public void snackCopyCard() {
        updateListView();

        new SnackBar.Builder(this)
                .withMessageId(R.string.copied_card)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Indicates that a card has been moved
     */
    public void snackMoveCard() {
        updateListView();

        new SnackBar.Builder(this)
                .withMessageId(R.string.moved_card)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }

    /**
     * Toggles the favorite state of an item
     *
     * @param favorite whether or not a card has been added to favorites
     */
    public void toggleFavorite(boolean favorite) {
        updateListView();

        new SnackBar.Builder(this)
                .withMessageId(favorite ? R.string.add_card_to_favorites : R.string.remove_card_from_favorites)
                .withStyle(SnackBar.Style.INFO)
                .withDuration(SnackBar.SHORT_SNACK)
                .show();
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_cards;
    }

    /**
     * Updates the list view
     */
    private void updateListView() {
        cardsAdapter.filter();
        slv.closeOpenedItems();
        slv.invalidateViews();
        checkEmptyStack();
        updateCardCount();
    }

    private int getFirst() {
        int first = slv.getFirstVisiblePosition();
        if (slv.getChildAt(0).getTop() < 0)
            first++;

        return first;
    }

    private void checkEmptyStack() {
        phNoCards.setVisibility(cardsAdapter.getFilteredItems().isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateCardCount() {
        toolbarTextView.setText(String.valueOf(cardsController.getVisibleCardCount()));
    }

    /**
     * Displays an alert that at least one answer shall be selected
     */
    public void alertSelectAnswer() {
        new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.select_answer)
                .withStyle(SnackBar.Style.ALERT)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }
}