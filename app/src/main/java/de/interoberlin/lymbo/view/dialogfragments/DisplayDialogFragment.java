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
        if (b.getString("type").equals("CREATE_STACK")) {
            type = EDialogType.CREATE_STACK;
        } else if (b.getString("type").equals("CHANGE_STACK")) {
            type = EDialogType.CHANGE_STACK;
        } else if (b.getString("type").equals("DISCARD_STACK")) {
            type = EDialogType.DISCARD_STACK;
        } else if (b.getString("type").equals("DISCARD_CARD")) {
            type = EDialogType.DISCARD_CARD;
        } else if (b.getString("type").equals("HINT")) {
            type = EDialogType.HINT;
        }

        builder.setView(v);
        builder.setTitle((String) b.get("title"));
        builder.setMessage((String) b.get("message"));

        // Add action listeners
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();

                switch (type) {
                    case HINT: {
                        ocListener.onHintDialogComplete();
                        break;
                    }
                    case DISCARD_CARD: {
                        ocListener.onDiscardCardDialogComplete();
                        break;
                    }
                    case DISCARD_STACK: {
                        ocListener.onDiscardStackDialogComplete();
                        break;
                    }
                    default: {
                        break;
                    }
                }

            }
        });

        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

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