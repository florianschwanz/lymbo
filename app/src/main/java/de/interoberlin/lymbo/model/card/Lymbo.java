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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.view.dialogfragments.DisplayDialogFragment;
import de.interoberlin.lymbo.view.dialogfragments.EDialogType;
import de.interoberlin.mate.lib.util.Toaster;

public class Lymbo implements Displayable {
    private String path;
    private boolean asset;

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

        tvTitle.setText(getTitle());
        tvSubtitle.setText(getSubtitle());

        ivDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add(a.getResources().getString(R.string.not_yet_implemented));
            }
        });

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.add(a.getResources().getString(R.string.not_yet_implemented));
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

        return v;
    }

    @Override
    public View getEditableView(Context c, final Activity a, ViewGroup parent) {
        return new View(c);
    }

    /**
     * Returns a list of all tags used in this lymbo
     *
     * @return a list of tags
     */
    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();

        for (Card c : getCards()) {
            for (Tag t : c.getTags()) {
                if (!containsTag(tags, t)) {
                    tags.add(t);
                }
            }
        }

        Collections.sort(tags, new Comparator<Tag>() {
            @Override
            public int compare(Tag t1, Tag t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });

        return tags;
    }

    /**
     * Returns a list of all chapters used in this lymbo
     *
     * @return a list of tags representing the chapters found
     */
    public List<Tag> getChapters() {
        List<Tag> chapters = new ArrayList<>();

        for (Card c : getCards()) {
            if (c.getChapter() != null && !containsTag(chapters, c.getChapter())) {
                chapters.add(c.getChapter());
            }
        }

        Collections.sort(chapters, new Comparator<Tag>() {
            @Override
            public int compare(Tag t1, Tag t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });

        return chapters;
    }

    private boolean containsTag(List<Tag> tags, Tag tag) {
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(tag.getName()))
                return true;
        }

        return false;
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAsset() {
        return asset;
    }

    public void setAsset(boolean asset) {
        this.asset = asset;
    }

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
            cards = new ArrayList<>();
        }

        cards.add(card);
    }
}
