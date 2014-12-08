package de.interoberlin.lymbo.model.card;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.view.activities.LymbosActivity;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;

public class Lymbo implements Displayable {
    private String title = "";
    private String subtitle = "";
    private String hint = "";
    private String image = "";
    private String author = "";
    private List<Card> cards;

    // -------------------------
    // Constructors
    // -------------------------

    public Lymbo() {
    }

    // -------------------------
    // Methods
    // -------------------------

    @Override
    public View getView(Context c, final Activity a, ViewGroup parent) {
        CardView v = (CardView) a.getLayoutInflater().inflate(R.layout.stack, null);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        TextView tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
        ImageView ivDiscard = (ImageView) v.findViewById(R.id.ivDiscard);
        ImageView ivEdit = (ImageView) v.findViewById(R.id.ivEdit);
        ImageView ivShare = (ImageView) v.findViewById(R.id.ivShare);
        ImageView ivUpload = (ImageView) v.findViewById(R.id.ivUpload);
        ImageView ivHint = (ImageView) v.findViewById(R.id.ivHint);
        ImageView ivLogo = (ImageView) v.findViewById(R.id.ivLogo);

        tvTitle.setText(getTitle());
        tvSubtitle.setText(getSubtitle());

        ivDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        ivHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
                Bundle b = new Bundle();
                b.putCharSequence("type", EDialogType.HINT.toString());
                b.putCharSequence("title", a.getResources().getString(R.string.hint));
                b.putCharSequence("message", getHint());

                displayDialogFragment.setArguments(b);
                displayDialogFragment.show(a.getFragmentManager(), "okay");
            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LymbosActivity.uiToast(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        return v;
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card card) {
        if (cards == null) {
            cards = new ArrayList<Card>();
        }

        cards.add(card);
    }
}
