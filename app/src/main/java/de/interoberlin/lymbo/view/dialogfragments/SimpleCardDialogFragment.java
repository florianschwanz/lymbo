package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.view.controls.RobotoEditText;

public class SimpleCardDialogFragment extends DialogFragment {
    private static EDialogType type;

    private RobotoEditText retFront;
    private RobotoEditText retBack;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public SimpleCardDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialogfragment_simplecard, null);

        retFront = (RobotoEditText) v.findViewById(R.id.retFront);
        retBack = (RobotoEditText) v.findViewById(R.id.retBack);

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
                        ocListener.onAddSimpleCard(retFront.getText().toString(), retBack.getText().toString());
                        dismiss();
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