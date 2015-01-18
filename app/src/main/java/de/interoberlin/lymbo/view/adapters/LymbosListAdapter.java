package de.interoberlin.lymbo.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.mate.lib.util.Toaster;

public class LymbosListAdapter extends ArrayAdapter<Lymbo> {
    Context c;

    // Controllers
    CardsController cardsController = CardsController.getInstance();


    // --------------------
    // Constructors
    // --------------------

    public LymbosListAdapter(Context context, int resource, List<Lymbo> items) {
        super(context, resource, items);

        this.c = context;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Lymbo lymbo = getItem(position);

        // Layout inflater
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        LinearLayout ll = (LinearLayout) vi.inflate(R.layout.stack, null);

        // Load views
        TextView tvTitle = (TextView) ll.findViewById(R.id.tvTitle);
        TextView tvSubtitle = (TextView) ll.findViewById(R.id.tvSubtitle);
        ImageView ivDiscard = (ImageView) ll.findViewById(R.id.ivDiscard);
        ImageView ivEdit = (ImageView) ll.findViewById(R.id.ivEdit);
        ImageView ivShare = (ImageView) ll.findViewById(R.id.ivShare);
        ImageView ivUpload = (ImageView) ll.findViewById(R.id.ivUpload);
        ImageView ivHint = (ImageView) ll.findViewById(R.id.ivHint);
        ImageView ivLogo = (ImageView) ll.findViewById(R.id.ivLogo);

        // Set values
        tvTitle.setText(lymbo.getTitle());
        tvSubtitle.setText(lymbo.getSubtitle());

        // Action : open cards view
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardsController.setLymbo(lymbo);
                cardsController.init();
                Intent openStartingPoint = new Intent(c, CardsActivity.class);
                c.startActivity(openStartingPoint);
            }
        });

        // Action : discard
        if (lymbo.getPath() != null) {
            ivDiscard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardsController.setLymbo(lymbo);
                    cardsController.discard();
                    notifyDataSetChanged();
                }
            });
        } else {
            remove(ivDiscard);
        }

        // Action : edit
        if (lymbo.getPath() != null) {
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toaster.add("Not yet implemented");
                }
            });
        } else {
            remove(ivEdit);
        }

        // Action : edit
        if (lymbo.getPath() != null) {
            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toaster.add("Not yet implemented");
                }
            });
        } else {
            remove(ivShare);
        }

        // Action : upload
        if (lymbo.getPath() != null) {
            ivUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toaster.add("Not yet implemented");
                }
            });
        } else {
            remove(ivUpload);
        }

        // Action : path
        if (lymbo.getPath() != null) {
            ivHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lymbo.getPath() != null)
                        Toaster.add(lymbo.getPath());
                    else
                        Toaster.add("lymbo stack from assets");
                }
            });
        } else {
            remove(ivHint);
        }

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Not yet implemented");
            }
        });

        return ll;
    }

    private void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }
}