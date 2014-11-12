package de.interoberlin.lymbo.view.card;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.interoberlin.lymbo.R;

public class DisplayStack extends CardView {
    private int id = 0;
    private Activity a;

    private String title = "";
    private String text = "";
    private String file;
    private String image;
    private String hint;
    private TextView tvTitle;
    private TextView tvText;
    private ImageView ivEdit;
    private ImageView ivDiscard;
    private ImageView ivShare;
    private ImageView ivUpload;
    private ImageView ivHint;
    private ImageView ivLogo;

    // --------------------
    // Constructors
    // --------------------

    public DisplayStack(Context c) {
        super(c);

        // Load layout
        initView(c);

        // Load vies
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvText = (TextView) findViewById(R.id.tvText);
        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        ivDiscard = (ImageView) findViewById(R.id.ivDiscard);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivUpload = (ImageView) findViewById(R.id.ivUpload);
        ivHint = (ImageView) findViewById(R.id.ivHint);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);

        // Title
        tvTitle.setText(title);
        tvText.setHint(hint);
    }

    public DisplayStack(Context c, AttributeSet attrs) {
        super(c, attrs);

        // Load layout
        initView(c);

        // Load vies
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvText = (TextView) findViewById(R.id.tvText);
        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        ivDiscard = (ImageView) findViewById(R.id.ivDiscard);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivUpload = (ImageView) findViewById(R.id.ivUpload);
        ivHint = (ImageView) findViewById(R.id.ivHint);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);

        // Title
        tvTitle.setText(title);
        tvText.setHint(hint);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.display_stack, null);
        addView(view);
    }

    // --------------------
    // Methods
    // --------------------

    // --------------------
    // Getters / Setters
    // --------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }

    public TextView getTvText() {
        return tvText;
    }

    public void setTvText(TextView tvText) {
        this.tvText = tvText;
    }

    public ImageView getIvEdit() {
        return ivEdit;
    }

    public void setIvEdit(ImageView ivEdit) {
        this.ivEdit = ivEdit;
    }

    public ImageView getIvDiscard() {
        return ivDiscard;
    }

    public void setIvDiscard(ImageView ivDiscard) {
        this.ivDiscard = ivDiscard;
    }

    public ImageView getIvShare() {
        return ivShare;
    }

    public void setIvShare(ImageView ivShare) {
        this.ivShare = ivShare;
    }

    public ImageView getIvUpload() {
        return ivUpload;
    }

    public void setIvUpload(ImageView ivUpload) {
        this.ivUpload = ivUpload;
    }

    public ImageView getIvHint() {
        return ivHint;
    }

    public void setIvHint(ImageView ivHint) {
        this.ivHint = ivHint;
    }

    public ImageView getIvLogo() {
        return ivLogo;
    }

    public void setIvLogo(ImageView ivLogo) {
        this.ivLogo = ivLogo;
    }
}

