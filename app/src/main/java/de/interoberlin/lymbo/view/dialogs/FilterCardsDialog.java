package de.interoberlin.lymbo.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;

public class FilterCardsDialog extends DialogFragment {
    public static final String TAG = FilterCardsDialog.class.getCanonicalName();

    private boolean displayOnlyFavorites;

    private OnCompleteListener ocListener;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialog_filter_cards, null);
        final TextView tvFavorite = (TextView) v.findViewById(R.id.tvFavorite);
        final TableLayout tblTags = (TableLayout) v.findViewById(R.id.tblTags);
        final TextView tvAll = (TextView) v.findViewById(R.id.tvAll);
        final TextView tvNone = (TextView) v.findViewById(R.id.tvNone);

        // Get arguments
        Bundle bundle = this.getArguments();
        final ArrayList<String> tagsAll = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_tags_all));
        final ArrayList<String> tagsSelected = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_tags_selected));
        displayOnlyFavorites = bundle.getBoolean(getActivity().getResources().getString(R.string.bundle_display_only_favorites));

        // Fill views with arguments
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.filter);

        if (displayOnlyFavorites)
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_important), null);
        else
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_not_important), null);

        if (tagsAll != null) {
            for (final String t : tagsAll) {
                final TableRow tr = new TableRow(getActivity());
                final CheckBox cb = new CheckBox(getActivity());
                final TextView tvText = new TextView(getActivity());

                tr.addView(cb);
                tr.addView(tvText);

                if (tagsSelected != null) {
                    for (String ts : tagsSelected) {
                        if (ts.equals(t)) {
                            cb.setChecked(true);
                        }
                    }
                }

                tvText.setText(t);
                tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb.toggle();
                    }
                });

                tblTags.addView(tr);
            }
        }

        // Add actions
        tvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDisplayOnlyFavorites(tvFavorite);
            }
        });

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

        AlertDialog dialog = (AlertDialog) getDialog();
        final TableLayout tblTags = (TableLayout) dialog.findViewById(R.id.tblTags);

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Tag> tagsSelected = new ArrayList<>();

                for (int i = 0; i < tblTags.getChildCount(); i++) {
                    final TableRow tr = (TableRow) tblTags.getChildAt(i);
                    final CheckBox cb = (CheckBox) tr.getChildAt(0);
                    final TextView tvText = (TextView) tr.getChildAt(1);

                    if (cb.isChecked())
                        tagsSelected.add(new Tag(tvText.getText().toString()));
                }

                ocListener.onFilterCards(tagsSelected, displayOnlyFavorites);

                dismiss();
            }
        });
    }

    // --------------------
    // Methods - Actions
    // --------------------

    private void toggleDisplayOnlyFavorites(TextView tvFavorite) {
        if (displayOnlyFavorites) {
            displayOnlyFavorites = false;
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_not_important), null);
        } else {
            displayOnlyFavorites = true;
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_important), null);
        }
    }

    private void setAllTagsTo(TableLayout tblTags, boolean value) {
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

    public interface OnCompleteListener {
        void onFilterCards(List<Tag> tagsSelected, boolean displayOnlyFavorites);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.ocListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTagsSelectedListener");
        }
    }
}