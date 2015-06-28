/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.interoberlin.lymbo.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.ListView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.ComponentsController;
import de.interoberlin.lymbo.view.adapters.ComponentsListAdapter;
import de.interoberlin.lymbo.view.controls.ComponentsListView;

/**
 * This application creates a listview where the ordering of the data set
 * can be modified in response to user touch events.
 * <p/>
 * An item in the listview is selected via a long press event and is then
 * moved around by tracking and following the movement of the user's finger.
 * When the item is released, it animates to its new position within the listview.
 */
public class EditCardActivity extends BaseActivity {
    // Controllers
    ComponentsController componentsController = ComponentsController.getInstance();

    // Context and Activity
    private static Context context;
    // private static Activity activity;

    ComponentsListAdapter componentsAdapter;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_ab_drawer);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // Get activity and context for further use
        // activity = this;
        context = getApplicationContext();

        // Get list view and add adapter
        ComponentsListView clv = (ComponentsListView) findViewById(R.id.clvComponents);
        componentsAdapter = new ComponentsListAdapter(this, this, 0, componentsController.getCard().getSides().get(0).getComponents());
        clv.setComponents(componentsController.getCard().getSides().get(0).getComponents());
        clv.setAdapter(componentsAdapter);
        clv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }


    // --------------------
    // Methods
    // --------------------

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_card_edit;
    }
}
