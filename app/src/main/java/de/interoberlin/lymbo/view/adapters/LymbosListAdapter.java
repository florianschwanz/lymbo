package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.persistence.LymboLoader;
import de.interoberlin.lymbo.model.share.MailSender;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.view.activities.CardsActivity;
import de.interoberlin.lymbo.view.activities.LymbosActivity;

public class LymbosListAdapter extends ArrayAdapter<Lymbo> {
    private Context c;
    private Activity a;

    // Controllers
    private CardsController cardsController = CardsController.getInstance();

    // --------------------
    // Constructors
    // --------------------

    public LymbosListAdapter(Activity activity, Context context, int resource, List<Lymbo> items) {
        super(context, resource, items);
        this.a = activity;
        this.c = context;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Lymbo lymbo = getItem(position);

        if (lymbo != null) {

            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            LinearLayout ll = (LinearLayout) vi.inflate(R.layout.stack, parent, false);

            // Load views
            ImageView ivImage = (ImageView) ll.findViewById(R.id.ivImage);
            TextView tvTitle = (TextView) ll.findViewById(R.id.tvTitle);
            TextView tvSubtitle = (TextView) ll.findViewById(R.id.tvSubtitle);
            ImageView ivStash = (ImageView) ll.findViewById(R.id.ivStash);
            ImageView ivEdit = (ImageView) ll.findViewById(R.id.ivEdit);
            ImageView ivShare = (ImageView) ll.findViewById(R.id.ivShare);
            ImageView ivUpload = (ImageView) ll.findViewById(R.id.ivUpload);
            ImageView ivHint = (ImageView) ll.findViewById(R.id.ivHint);

            // Set values
            if (lymbo.getImage() != null) {
                Bitmap b = Base64BitmapConverter.decodeBase64(lymbo.getImage());
                BitmapDrawable bd = new BitmapDrawable(b);
                ivImage.setBackgroundDrawable(bd);
            }
            if (lymbo.getTitle() != null)
                tvTitle.setText(lymbo.getTitle());
            if (lymbo.getSubtitle() != null)
                tvSubtitle.setText(lymbo.getSubtitle());

            // Action : open cards view
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Lymbo l;

                    if (lymbo.isAsset()) {
                        l = LymboLoader.getLymboFromAsset(c, lymbo.getPath(), false);
                    } else {
                        l = LymboLoader.getLymboFromFile(new File(lymbo.getPath()), false);
                    }

                    if (l != null) {
                        cardsController.setLymbo(l);
                        cardsController.init();
                        Intent openStartingPoint = new Intent(c, CardsActivity.class);
                        c.startActivity(openStartingPoint);
                    }
                }
            });

            // Action : stash
            ivStash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardsController.setLymbo(lymbo);
                    ((LymbosActivity) a).stash();
                }
            });

            // Action : edit
        /*if (!lymbo.isAsset()) {
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, R.string.not_yet_implemented, Toast.LENGTH_SHORT);
                }
            });
        } else {*/
            remove(ivEdit);
        /*}*/

            // Action : send
            if (!lymbo.isAsset()) {
                ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MailSender.sendLymbo(c, a, lymbo);
                    }
                });
            } else {
                remove(ivShare);
            }

            // Action : upload
        /*
        if (!lymbo.isAsset()) {
            ivUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, R.string.not_yet_implemented, Toast.LENGTH_SHORT);
                }
            });
        } else {*/
            remove(ivUpload);
        /*}*/

            // Action : hint
        /*
        if (!lymbo.isAsset()) {
            ivHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, lymbo.getPath(), Toast.LENGTH_SHORT);
                }
            });
        } else {*/
            remove(ivHint);
        /*}*/

            return ll;
        } else {
            // Layout inflater
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            return (LinearLayout) vi.inflate(R.layout.toolbar_space, parent, false);
        }


    }

    private void remove(View v) {
        ((ViewManager) v.getParent()).removeView(v);
    }
}