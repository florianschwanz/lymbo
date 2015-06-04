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

public class AddSimpleCardDialogFragment extends DialogFragment {
    private static EDialogType type = EDialogType.NULL;

    private EditText etFront;
    private EditText etBack;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public AddSimpleCardDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context c = getActivity();

        // Load layout
        final View v = View.inflate(c, R.layout.dialogfragment_simplecard, null);

        etFront = (EditText) v.findViewById(R.id.etFront);
        etBack = (EditText) v.findViewById(R.id.etBack);

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle b = getArguments();

        // Determine type
        switch (b.getString("type")) {
            case "ADD_SIMPLE_CARD":
                type = EDialogType.ADD_SIMPLE_CARD;
                break;
        }

        builder.setView(v);
        builder.setTitle((String) b.get("title"));
        builder.setMessage((String) b.get("message"));

        // Add positive button
        switch (type) {
            case ADD_SIMPLE_CARD: {
                builder.setPositiveButton(R.string.okay, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String front = etFront.getText().toString().trim();

                        Drawable dWarning = c.getResources().getDrawable(R.drawable.ic_action_warning);
                        boolean valid = true;

                        if (front.isEmpty()) {
                            etFront.setError(c.getResources().getString(R.string.field_must_not_be_empty), dWarning);
                            valid = false;
                        }

                        if (valid) {
                            ocListener.onAddSimpleCard(etFront.getText().toString(), etBack.getText().toString());
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
            case ADD_SIMPLE_CARD: {
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
        public abstract void onAddSimpleCard(String frontText, String backText);
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