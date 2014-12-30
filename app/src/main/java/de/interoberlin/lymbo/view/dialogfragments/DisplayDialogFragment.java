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

public class DisplayDialogFragment extends DialogFragment {
    private static EDialogType type;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public DisplayDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialogfragment, null);

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle b = getArguments();

        // Determine type
        switch (b.getString("type")) {
            case "CREATE_STACK":
                type = EDialogType.CREATE_STACK;
                break;
            case "CHANGE_STACK":
                type = EDialogType.CHANGE_STACK;
                break;
            case "DISCARD_STACK":
                type = EDialogType.DISCARD_STACK;
                break;
            case "DISCARD_CARD":
                type = EDialogType.DISCARD_CARD;
                break;
            case "HINT":
                type = EDialogType.HINT;
                break;
            case "WARNING":
                type = EDialogType.WARNING;
                break;
        }

        builder.setView(v);
        builder.setTitle((String) b.get("title"));
        builder.setMessage((String) b.get("message"));

        // Add positive button
        switch (type) {
            case HINT: {
                builder.setPositiveButton(R.string.okay, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ocListener.onHintDialogComplete();
                        dismiss();
                    }
                });
                break;
            }
            case DISCARD_CARD: {
                builder.setPositiveButton(R.string.okay, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ocListener.onDiscardCardDialogComplete();
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
            case DISCARD_CARD:
            case DISCARD_STACK:
            case DOWNLOAD_BLOB: {
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
        // Call super
        super.onPause();
    }

// --------------------
// Callback interfaces
// --------------------

    public static interface OnCompleteListener {
        public abstract void onHintDialogComplete();

        public abstract void onDiscardStackDialogComplete();

        public abstract void onDiscardCardDialogComplete();

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