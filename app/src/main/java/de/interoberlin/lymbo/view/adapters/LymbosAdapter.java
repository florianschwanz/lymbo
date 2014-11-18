package de.interoberlin.lymbo.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.XmlLymbo;
import de.interoberlin.lymbo.view.activities.LymbosActivity;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;

public class LymbosAdapter extends RecyclerView.Adapter<LymbosAdapter.ViewHolder> {
    private Activity a;
    private Context c;

    private List<XmlLymbo> dataset;
    private boolean swipeable;
    private Resources resources;

    // --------------------
    // Constructors
    // --------------------

    public LymbosAdapter(Activity a, Context c, List<XmlLymbo> dataset, boolean swipeable) {
        this.a = a;
        this.c = c;
        this.dataset = dataset;
        this.swipeable = swipeable;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public LymbosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stack, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final XmlLymbo lymbo = dataset.get(position);
        viewHolder.tvTitle.setText(lymbo.getTitle());
        viewHolder.tvSubtitle.setText(lymbo.getSubtitle());

        viewHolder.ivDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        viewHolder.ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        viewHolder.ivHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
                Bundle b = new Bundle();
                b.putCharSequence("type", EDialogType.HINT.toString());
                b.putCharSequence("title", a.getResources().getString(R.string.hint));
                b.putCharSequence("message", lymbo.getHint());

                displayDialogFragment.setArguments(b);
                displayDialogFragment.show(a.getFragmentManager(), "okay");
            }
        });

        viewHolder.ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }

    // --------------------
    // Inner Classes
    // --------------------

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvSubtitle;
        public ImageView ivDiscard;
        public ImageView ivEdit;
        public ImageView ivShare;
        public ImageView ivUpload;
        public ImageView ivHint;
        public ImageView ivLogo;

        // --------------------
        // Constructors
        // --------------------

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
            ivDiscard = (ImageView) v.findViewById(R.id.ivDiscard);
            ivEdit = (ImageView) v.findViewById(R.id.ivEdit);
            ivShare = (ImageView) v.findViewById(R.id.ivShare);
            ivUpload = (ImageView) v.findViewById(R.id.ivUpload);
            ivHint = (ImageView) v.findViewById(R.id.ivHint);
            ivLogo = (ImageView) v.findViewById(R.id.ivLogo);
        }

        // --------------------
        // Getters / Setters
        // --------------------

    }
}