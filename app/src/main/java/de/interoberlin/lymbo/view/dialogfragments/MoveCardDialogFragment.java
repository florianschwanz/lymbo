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
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class MoveCardDialogFragment extends DialogFragment {
    // Controllers
    private StacksController stacksController;

    private List<CheckBox> checkboxes = new ArrayList<>();
    private String sourceLymboId = null;
    private String targetLymboId = null;
    private String cardUuid = null;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public MoveCardDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stacksController = StacksController.getInstance(getActivity());

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_move_card, null);
        final TableLayout tblLymbos = (TableLayout) v.findViewById(R.id.tblLymbos);

        // Get arguments
        Bundle bundle = this.getArguments();
        sourceLymboId = bundle.getString(getActivity().getResources().getString(R.string.bundle_lymbo_uuid));
        cardUuid = bundle.getString(getActivity().getResources().getString(R.string.bundle_card_uuid));

        for (final Stack l : stacksController.getStacks()) {
            if (!l.getId().equals(sourceLymboId)) {
                final TableRow tr = new TableRow(getActivity());

                final CheckBox cb = new CheckBox(getActivity());
                final RobotoTextView tvText = new RobotoTextView(getActivity());

                checkboxes.add(cb);

                tr.addView(cb);
                tr.addView(tvText);

                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        targetLymboId = l.getId();
                        if (b) {
                            for (CheckBox c : checkboxes) {
                                c.setChecked(false);
                            }

                            cb.setChecked(true);
                        }
                    }
                });

                tvText.setText(l.getTitle());
                tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        targetLymboId = l.getId();
                        cb.toggle();
                    }
                });

                tblLymbos.addView(tr);
            }
        }

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.move_card);

        // Add positive button
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ocListener.onMoveCard(sourceLymboId, targetLymboId, cardUuid);
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
        void onMoveCard(String sourceLymboId, String targetLymboId, String cardId);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.ocListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMoveCardListener");
        }
    }
}