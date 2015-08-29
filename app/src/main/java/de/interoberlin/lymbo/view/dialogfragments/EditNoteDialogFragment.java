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

public class EditNoteDialogFragment extends DialogFragment {
    private EditText etNote;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public EditNoteDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context c = getActivity();

        // Load layout
        final View v = View.inflate(c, R.layout.dialogfragment_edit_note, null);

        etNote = (EditText) v.findViewById(R.id.etNote);

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        final String note = getArguments().getString("note");
        final String uuid = getArguments().getString("uuid");

        etNote.setText(note);

        // Add positive button
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String note = etNote.getText().toString().trim();

                Drawable dWarning = c.getResources().getDrawable(R.drawable.ic_action_warning);
                boolean valid = true;

                if (note.isEmpty()) {
                    etNote.setError(c.getResources().getString(R.string.field_must_not_be_empty), dWarning);
                    valid = false;
                }

                if (valid) {
                    ocListener.onEditNote(uuid, note);
                    dismiss();
                }
            }
        });

        // Add negative button
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    // --------------------
    // Callback interfaces
    // --------------------

    public interface OnCompleteListener {
        void onEditNote(String uuid, String editNote);
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