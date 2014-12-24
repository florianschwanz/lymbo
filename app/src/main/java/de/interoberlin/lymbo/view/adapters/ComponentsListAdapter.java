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

package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import de.interoberlin.lymbo.controller.ComponentsController;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;

public class ComponentsListAdapter extends ArrayAdapter<Displayable> {
    Context c;
    Activity a;

    // Controllers
    ComponentsController componentsController = ComponentsController.getInstance();

    HashMap<Displayable, Integer> idMap = new HashMap<Displayable, Integer>();

    final int INVALID_ID = -1;

    // --------------------
    // Constructors
    // --------------------

    public ComponentsListAdapter(Context context, Activity activity, int resource, List<Displayable> items) {
        super(context, resource, items);

        this.c = context;
        this.a = activity;

        for (int i = 0; i < items.size(); ++i) {
            idMap.put(items.get(i), i);
        }
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= idMap.size()) {
            return INVALID_ID;
        }
        Displayable component = getItem(position);
        return idMap.get(component);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());

        if (getItem(position) instanceof TitleComponent || getItem(position) instanceof TextComponent) {
            return getItem(position).getEditableView(c, a, null);
        }

        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
