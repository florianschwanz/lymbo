package de.interoberlin.lymbo.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.LymbosActivity;
import de.interoberlin.mate.lib.util.Toaster;

public class LymbosListAdapter extends ArrayAdapter<Lymbo> {
    Context c;

    // Controllers
    CardsController cardsController = CardsController.getInstance();


    // --------------------
    // Constructors
    // --------------------

    public LymbosListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.c = context;
    }

    public LymbosListAdapter(Context context, int resource, List<Lymbo> items) {
        super(context, resource, items);

        this.c = context;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        CardView cv = (CardView) vi.inflate(R.layout.stack, null);

        TextView tvTitle = (TextView) cv.findViewById(R.id.tvTitle);
        TextView tvSubtitle = (TextView) cv.findViewById(R.id.tvSubtitle);
        ImageView ivDiscard = (ImageView) cv.findViewById(R.id.ivDiscard);
        ImageView ivEdit = (ImageView) cv.findViewById(R.id.ivEdit);
        ImageView ivShare = (ImageView) cv.findViewById(R.id.ivShare);
        ImageView ivUpload = (ImageView) cv.findViewById(R.id.ivUpload);
        ImageView ivHint = (ImageView) cv.findViewById(R.id.ivHint);
        ImageView ivLogo = (ImageView) cv.findViewById(R.id.ivLogo);

        final Lymbo lymbo = getItem(position);
        tvTitle.setText(lymbo.getTitle());
        tvSubtitle.setText(lymbo.getSubtitle());

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardsController.setLymbo(lymbo);
                cardsController.init();
                Intent openStartingPoint = new Intent(((LymbosActivity) c), CardsActivity.class);
                ((LymbosActivity) c).startActivity(openStartingPoint);
            }
        });

        ivDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Not yet implemented");
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Not yet implemented");
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Not yet implemented");
            }
        });

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Not yet implemented");
            }
        });

        ivHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lymbo.getPath() != null)
                    Toaster.add(lymbo.getPath());
                else
                    Toaster.add("lymbo stack from assets");
            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add("Not yet implemented");
            }
        });

        if (lymbo.getPath() == null) {
            remove(ivDiscard);
        }

        return cv;
    }

    private void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }
}