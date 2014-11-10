package de.interoberlin.lymbo.view.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.util.Base64BitmapConverter;
import de.interoberlin.lymbo.view.activities.StacksActivity;
import de.interoberlin.mate.lib.model.Log;

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

    public DisplayStack(int id, Activity a) {
        super(a);
        this.id = id;
    }

    public DisplayStack(int id, Activity a, String title, String text, String image, String hint, String file) {
        super(a);

        this.id = id;
        this.a = a;
        this.title = title;
        this.text = text;
        this.image = image;
        this.hint = hint;
        this.file = file;
    }

    // --------------------
    // Methods
    // --------------------

    public View getCardContent(Context context) {
        Log.trace("DisplayStack.getCardContent()");

        // Load layout
        View view = LayoutInflater.from(context).inflate(R.layout.display_stack, null);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (a.getResources()
                .getDimension(R.dimen.stack_height))));

        // Load vies
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvText = (TextView) view.findViewById(R.id.tvText);
        ivEdit = (ImageView) view.findViewById(R.id.ivEdit);
        ivDiscard = (ImageView) view.findViewById(R.id.ivDiscard);
        ivShare = (ImageView) view.findViewById(R.id.ivShare);
        ivUpload = (ImageView) view.findViewById(R.id.ivUpload);
        ivHint = (ImageView) view.findViewById(R.id.ivHint);
        ivLogo = (ImageView) view.findViewById(R.id.ivLogo);

        // Title
        tvTitle.setText(title);

        // Text
        tvText.setText(text);

        // Edit
        ivEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                InputDialogFragment inputDialogFragment = new InputDialogFragment();
				Bundle b = new Bundle();
				b.putString("type", "CHANGE_STACK");
				b.putString("file", file);
				b.putString("title", a.getResources().getString(R.string.txtChangeName));
				b.putString("message", "What shall be the new name");
				b.putString("hint", title);

				inputDialogFragment.setArguments(b);
				inputDialogFragment.show(a.getFragmentManager(), "okay");
				*/
            }
        });

        // Discard
        ivDiscard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
				// Make current settings available
				Properties.setCurrentFileString(file);

				DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
				Bundle b = new Bundle();
				b.putCharSequence("type", EDialogType.DISCARD_STACK.toString());
				b.putCharSequence("title", a.getResources().getString(R.string.txtDiscard));
				b.putCharSequence("message", a.getResources().getString(R.string.txtDiscardStack));

				displayDialogFragment.setArguments(b);
				displayDialogFragment.show(a.getFragmentManager(), "okay");
				*/
            }
        });

        // Share
        ivShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Lymbo");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Here is a lymbo file I want to share with you");

                File fi = new File(file);

                if (!fi.exists() || !fi.canRead()) {
                    StacksActivity.uiToast("Attachment Error");
                    // finish();
                    return;
                }
                Uri uri = Uri.parse("file://" + file);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

                a.startActivity(Intent.createChooser(emailIntent, "Send mail"));
            }
        });

        // Upload
        ivUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StacksActivity.uiToast("Not yet implemented");
            }
        });

        // Hint
        ivHint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
				DisplayDialogFragment displayDialogFragment = new DisplayDialogFragment();
				Bundle b = new Bundle();
				b.putCharSequence("type", EDialogType.HINT.toString());
				b.putCharSequence("title", a.getResources().getString(R.string.txtHint));
				b.putCharSequence("message", hint);

				displayDialogFragment.setArguments(b);
				displayDialogFragment.show(a.getFragmentManager(), "okay");
				*/
            }
        });

        // Set background image
        if (!image.equals("")) {
            ivLogo.setBackgroundDrawable(new BitmapDrawable(Base64BitmapConverter.decodeBase64(image)));
        }

        return view;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}