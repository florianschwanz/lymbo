package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class CheckboxDialogFragment extends DialogFragment {
    // Controllers
    CardsController cardsController = CardsController.getInstance();

    private static EDialogType type;

    private OnLabelSelectedListener onLabelSelectedListener;

    // --------------------
    // Constructors
    // --------------------

    public CheckboxDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context c = getActivity();

        // Load layout
        final View v = View.inflate(c, R.layout.dialogfragment_table, null);
        final TableLayout tblChapters = (TableLayout) v.findViewById(R.id.tblChapters);
        final TableLayout tblTags = (TableLayout) v.findViewById(R.id.tblTags);

        for (final Tag t : cardsController.getLymbo().getChapters()) {
            final TableRow tr = new TableRow(c);

            final CheckBox cb = new CheckBox(c);
            final RobotoTextView tvText = new RobotoTextView(c);

            tr.addView(cb);
            tr.addView(tvText);

            cb.setChecked(t.isChecked());
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    t.setChecked(b);
                }
            });

            tvText.setText(t.getName());
            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.toggle();
                }
            });

            tblChapters.addView(tr);
        }

        for (final Tag t : cardsController.getLymbo().getTags()) {
            final TableRow tr = new TableRow(c);

            final CheckBox cb = new CheckBox(c);
            final RobotoTextView tvText = new RobotoTextView(c);

            tr.addView(cb);
            tr.addView(tvText);

            cb.setChecked(t.isChecked());
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    t.setChecked(b);
                }
            });

            tvText.setText(t.getName());
            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.toggle();
                }
            });

            tblTags.addView(tr);
        }


        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle b = getArguments();

        // Determine type
        switch (b.getString("type")) {
            case "ADD_STACK":
                type = EDialogType.ADD_STACK;
                break;
            case "CHANGE_STACK":
                type = EDialogType.CHANGE_STACK;
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
            case "SELECT_LABEL":
                type = EDialogType.SELECT_LABEL;
                break;
        }

        builder.setView(v);
        if (b.get("title") != null) {
            builder.setTitle((String) b.get("title"));
        }
        if (b.get("message") != null) {
            builder.setMessage((String) b.get("message"));
        }

        // Add positive button
        switch (type) {
            case SELECT_LABEL: {
                builder.setPositiveButton(R.string.okay, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onLabelSelectedListener.onLabelSelected();
                        dismiss();
                    }
                });
            }
        }

        // Add negative button
        switch (type) {
            case SELECT_LABEL: {
                break;
            }

            default: {
                builder.setNegativeButton(R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
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

    public static interface OnLabelSelectedListener {

        public abstract void onLabelSelected();

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.onLabelSelectedListener = (OnLabelSelectedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}