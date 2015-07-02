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
import android.widget.TextView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class SelectTagsDialogFragment extends DialogFragment {
    // Controllers
    CardsController cardsController;

    private OnLabelSelectedListener onLabelSelectedListener;

    // --------------------
    // Constructors
    // --------------------

    public SelectTagsDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardsController = CardsController.getInstance(getActivity());

        Context c = getActivity();

        // Load layout
        final View v = View.inflate(c, R.layout.dialogfragment_select_labels, null);
        final TableLayout tblChapters = (TableLayout) v.findViewById(R.id.tblChapters);
        final TableLayout tblTags = (TableLayout) v.findViewById(R.id.tblTags);
        final TextView tvAll = (TextView) v.findViewById(R.id.tvAll);
        final TextView tvNone = (TextView) v.findViewById(R.id.tvNone);

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

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllTagsTo(tblTags, true);
            }
        });

        tvNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllTagsTo(tblTags, false);
            }
        });

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
            builder.setTitle(R.string.tags);

        // Add positive button
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onLabelSelectedListener.onLabelSelected();
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


    private void setAllTagsTo(TableLayout tblTags, boolean value) {
        for (final Tag t : cardsController.getLymbo().getTags()) {
            t.setChecked(value);
        }

        for (int i = 0; i < tblTags.getChildCount(); i++) {
            if (tblTags.getChildAt(i) instanceof TableRow) {
                TableRow tr = (TableRow) tblTags.getChildAt(i);

                if (tr.getChildCount() > 0 && tr.getChildAt(0) instanceof CheckBox) {
                    ((CheckBox) tr.getChildAt(0)).setChecked(value);
                }
            }
        }
    }

    // --------------------
    // Callback interfaces
    // --------------------

    public interface OnLabelSelectedListener {
        void onLabelSelected();
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