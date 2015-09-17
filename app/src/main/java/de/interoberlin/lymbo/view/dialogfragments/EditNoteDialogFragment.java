package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.interoberlin.lymbo.R;

public class EditNoteDialogFragment extends DialogFragment {
    private OnCompleteListener ocListener;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_edit_note, null);
        final EditText etNote = (EditText) v.findViewById(R.id.etNote);

        // Get arguments
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        final String note = getArguments().getString(getActivity().getResources().getString(R.string.bundle_note));

        // Fill views with arguments
        etNote.setText(note);

        // Add positive button
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Add negative button
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get arguments
        Bundle bundle = this.getArguments();
        final String cardUuid = bundle.getString(getActivity().getResources().getString(R.string.bundle_card_uuid));

        AlertDialog dialog = (AlertDialog) getDialog();
        final EditText etNote = (EditText) dialog.findViewById(R.id.etNote);

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = etNote.getText().toString().trim();

                Drawable dWarning = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_warning);
                boolean valid = true;

                if (note.isEmpty()) {
                    etNote.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                    valid = false;
                }

                if (valid) {
                    ocListener.onEditNote(cardUuid, note);
                    dismiss();
                }
            }
        });
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