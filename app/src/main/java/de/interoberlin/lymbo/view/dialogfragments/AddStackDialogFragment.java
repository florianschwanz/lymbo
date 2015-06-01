package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import de.interoberlin.lymbo.R;

public class AddStackDialogFragment extends DialogFragment {
    private static EDialogType type = EDialogType.NULL;

    private EditText etTitle;
    private EditText etSubtitle;
    private EditText etAuthor;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public AddStackDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context c = getActivity();

        // Load layout
        final View v = View.inflate(c, R.layout.dialogfragment_addstack, null);

        etTitle = (EditText) v.findViewById(R.id.etTitle);
        etSubtitle = (EditText) v.findViewById(R.id.etSubtitle);
        etAuthor = (EditText) v.findViewById(R.id.etAuthor);

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle b = getArguments();

        // Determine type
        switch (b.getString("type")) {
            case "ADD_STACK":
                type = EDialogType.ADD_STACK;
                break;
        }

        builder.setView(v);
        builder.setTitle((String) b.get("title"));
        builder.setMessage((String) b.get("message"));

        // Add positive button
        switch (type) {
            case ADD_STACK: {
                builder.setPositiveButton(R.string.okay, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = etTitle.getText().toString().trim();
                        String subtitle = etSubtitle.getText().toString().trim();
                        String author = etAuthor.getText().toString().trim();

                        Drawable dWarning = c.getResources().getDrawable(R.drawable.ic_action_warning);
                        boolean valid = true;

                        if (title.isEmpty()) {
                            etTitle.setError(c.getResources().getString(R.string.field_must_not_be_empty), dWarning);
                            valid = false;
                        }

                        if (author.isEmpty()) {
                            etAuthor.setError(c.getResources().getString(R.string.field_must_not_be_empty), dWarning);
                            valid = false;
                        }

                        if (valid) {
                            ocListener.onAddStack(title, subtitle, author);
                            dismiss();
                        }
                    }
                });
                break;
            }
            default: {
                builder.setPositiveButton(R.string.okay, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                break;
            }
        }


        // Add negative button
        switch (type) {
            case ADD_STACK: {
                builder.setNegativeButton(R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                break;
            }
            default: {
                break;
            }
        }

        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

// --------------------
// Callback interfaces
// --------------------

    public static interface OnCompleteListener {
        public abstract void onAddStack(String title, String subtitle, String author);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.ocListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}