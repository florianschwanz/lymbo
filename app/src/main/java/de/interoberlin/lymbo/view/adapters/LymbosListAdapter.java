package de.interoberlin.lymbo.view.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.XmlLymbo;
import de.interoberlin.lymbo.view.activities.LymbosActivity;

public class LymbosListAdapter extends ArrayAdapter<XmlLymbo> {
    // --------------------
    // Constructors
    // --------------------

    public LymbosListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public LymbosListAdapter(Context context, int resource, List<XmlLymbo> items) {
        super(context, resource, items);
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


        final XmlLymbo lymbo = getItem(position);
        tvTitle.setText(lymbo.getTitle());
        tvSubtitle.setText(lymbo.getSubtitle());

        ivDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        ivHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
                Bundle b = new Bundle();
                b.putCharSequence("type", EDialogType.HINT.toString());
                b.putCharSequence("title", a.getResources().getString(R.string.hint));
                b.putCharSequence("message", lymbo.getHint());

                displayDialogFragment.setArguments(b);
                displayDialogFragment.show(a.getFragmentManager(), "okay");
                */
            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast("Not yet implemented");
            }
        });

        return cv;
    }
}