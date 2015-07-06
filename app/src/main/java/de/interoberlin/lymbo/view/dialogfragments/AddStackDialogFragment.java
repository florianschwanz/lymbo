package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.interoberlin.lymbo.R;

public class AddStackDialogFragment extends DialogFragment {
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

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_add_stack, null);

        etTitle = (EditText) v.findViewById(R.id.etTitle);
        etSubtitle = (EditText) v.findViewById(R.id.etSubtitle);
        etAuthor = (EditText) v.findViewById(R.id.etAuthor);

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.add_stack);

        // Add positive button
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = etTitle.getText().toString().trim();
                    String subtitle = etSubtitle.getText().toString().trim();
                    String author = etAuthor.getText().toString().trim();

                    Drawable dWarning = getActivity().getResources().getDrawable(R.drawable.ic_action_warning);

                    if (title.isEmpty()) {
                        etTitle.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                    } else {
                        ocListener.onAddStack(title, subtitle, author);
                        dismiss();
                    }
                }
            });
        }

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

    public interface OnCompleteListener {
        void onAddStack(String title, String subtitle, String author);
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