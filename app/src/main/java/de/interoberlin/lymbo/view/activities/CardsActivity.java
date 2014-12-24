package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.fortysevendeg.swipelistview.SwipeListView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.view.adapters.CardsListAdapter;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.mate.lib.util.Toaster;

public class CardsActivity extends BaseActivity implements DisplayDialogFragment.OnCompleteListener {
    // Controllers
    CardsController cardsController = CardsController.getInstance();
    LymbosController lymbosController = LymbosController.getInstance();

    // Context and Activity
    private static Context context;
    private static Activity activity;

    // Views
    private DrawerLayout drawer;
    private SwipeListView slv;

    private CardsListAdapter cardsAdapter;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_ab_drawer);

        // Register on toaster
        Toaster.register(this, context);

        drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // Get activity and context for further use
        activity = this;
        context = getApplicationContext();

        // Get list view and add adapter
        slv = (SwipeListView) findViewById(R.id.slv);
        cardsAdapter = new CardsListAdapter(this, this, R.layout.card, cardsController.getCards());
        slv.setAdapter(cardsAdapter);
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
    }

    public void onResume() {
        super.onResume();
        clear();
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
            case R.id.menu_add: {
                cardsController.addCard();
                cardsController.save();
                cardsAdapter = new CardsListAdapter(this, this, R.layout.card, cardsController.getCards());
                slv.setAdapter(cardsAdapter);

                break;
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

    public void uiRefresh() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear();
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