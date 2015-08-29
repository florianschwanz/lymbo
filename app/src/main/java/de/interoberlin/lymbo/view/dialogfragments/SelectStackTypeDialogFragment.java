package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.EStackType;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class SelectStackTypeDialogFragment extends DialogFragment {
    private List<CheckBox> checkboxes = new ArrayList<>();
    private List<String> types = new ArrayList<>();

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public SelectStackTypeDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_select_stack_types, null);
        final TableLayout tblStackTypes = (TableLayout) v.findViewById(R.id.tblStackTypes);

        for (final EStackType st : EStackType.values()) {
            types.add(st.getType());

            final TableRow tr = new TableRow(getActivity());

            final CheckBox cb = new CheckBox(getActivity());
            final RobotoTextView tvText = new RobotoTextView(getActivity());

            tr.addView(cb);
            tr.addView(tvText);
            checkboxes.add(cb);

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        for (CheckBox c : checkboxes) {
                            c.setChecked(false);
                        }

                        cb.setChecked(true);
                    }
                }
            });

            tvText.setText(st.getType());
            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.toggle();
                }
            });

            tblStackTypes.addView(tr);
        }

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.stack_types);

        // Add positive button
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i<checkboxes.size(); i++) {
                    if (checkboxes.get(i).isChecked()) {
                        ocListener.onStackTypeSelected(types.get(i));
                        dismiss();
                    }
                }

                dismiss();
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
        void onStackTypeSelected(String type);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.ocListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnStackTypeSelectedListener");
        }
    }
}