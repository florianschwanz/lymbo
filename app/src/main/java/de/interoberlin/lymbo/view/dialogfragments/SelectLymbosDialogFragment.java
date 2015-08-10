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
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class SelectLymbosDialogFragment extends DialogFragment {
    // Controllers
    private LymbosController lymbosController;

    private OnLymbosSelectedListener onLymbosSelectedListener;

    private List<CheckBox> checkboxes = new ArrayList<>();
    private Lymbo selectedLymbo = null;

    // --------------------
    // Constructors
    // --------------------

    public SelectLymbosDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lymbosController = LymbosController.getInstance(getActivity());

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_select_lymbos, null);
        final TableLayout tblLymbos = (TableLayout) v.findViewById(R.id.tblLymbos);

        for (final Lymbo l : lymbosController.getLymbos()) {
            final TableRow tr = new TableRow(getActivity());

            final CheckBox cb = new CheckBox(getActivity());
            final RobotoTextView tvText = new RobotoTextView(getActivity());

            checkboxes.add(cb);

            tr.addView(cb);
            tr.addView(tvText);

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    selectedLymbo = l;
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
                    selectedLymbo = l;
                    cb.toggle();
                }
            });

            tblLymbos.addView(tr);
        }

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.copy_card);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String cardId = bundle.getString(getActivity().getResources().getString(R.string.bundle_uuid));

        // Add positive button
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onLymbosSelectedListener.onLymbosSelected(selectedLymbo.getId(), cardId);
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

    public interface OnLymbosSelectedListener {
        void onLymbosSelected(String lymboId, String cardId);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.onLymbosSelectedListener = (OnLymbosSelectedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLymbosSelectedListener");
        }
    }
}