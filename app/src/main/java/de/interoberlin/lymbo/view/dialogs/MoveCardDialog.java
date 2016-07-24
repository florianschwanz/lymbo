package de.interoberlin.lymbo.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.StacksController;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;

public class MoveCardDialog extends DialogFragment {
    // <editor-fold defaultstate="expanded" desc="Members">

    public static final String TAG = MoveCardDialog.class.getSimpleName();

    private List<CheckBox> checkboxes = new ArrayList<>();
    private String targetLymboId = null;

    // View
    @BindView(R.id.tblStacks) TableLayout tblStacks;

    private OnCompleteListener ocListener;

    // </editor-fold>

    // --------------------
    // Methods - Lifecycle
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Lifecycle">

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StacksController stacksController = StacksController.getInstance();

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialog_move_card, null);
        ButterKnife.bind(this, v);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String sourceLymboId = bundle.getString(getActivity().getResources().getString(R.string.bundle_lymbo_uuid));

        // Fill views with arguments
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.move_card);

        for (final Stack l : stacksController.getStacks()) {
            if (!l.getId().equals(sourceLymboId)) {
                final TableRow tr = new TableRow(getActivity());

                final CheckBox cb = new CheckBox(getActivity());
                final TextView tvText = new TextView(getActivity());

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

                tblStacks.addView(tr);
            }
        }

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
        final String sourceLymboId = bundle.getString(getActivity().getResources().getString(R.string.bundle_lymbo_uuid));
        final String cardUuid = bundle.getString(getActivity().getResources().getString(R.string.bundle_card_uuid));

        AlertDialog dialog = (AlertDialog) getDialog();

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocListener.onMoveCard(sourceLymboId, targetLymboId, cardUuid);
            }
        });
    }

    // </editor-fold>

    // --------------------
    // Callback interfaces
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Callback interfaces">

    public interface OnCompleteListener {
        void onMoveCard(String sourceLymboId, String targetLymboId, String cardId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.ocListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + TAG);
        }
    }

    // </editor-fold>
}