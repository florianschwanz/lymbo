package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class TemplatesDialogFragment extends DialogFragment {
    private OnCompleteListener ocListener;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardsController cardsController = CardsController.getInstance(getActivity());

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_templates, null);
        final TableLayout tblTemplates = (TableLayout) v.findViewById(R.id.tblTemplates);

        // Get arguments
        Bundle bundle = this.getArguments();
        final ArrayList<String> templates = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_templates));

        // Fill views with arguments
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.templates);

        for (final String t : templates) {
            final TableRow tr = new TableRow(getActivity());
            final TextView tvText = new RobotoTextView(getActivity());

            final Card template = cardsController.getTemplateById(t);

            tvText.setText(template.getTitle());
            tvText.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
            tvText.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.add(0, 0, 0, getResources().getString(R.string.edit))
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    edit(template);
                                    return false;
                                }
                            });
                    contextMenu.add(0, 1, 0, getResources().getString(R.string.delete))
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    delete(template);
                                    return false;
                                }
                            });
                }
            });

            tr.addView(tvText);

            tblTemplates.addView(tr);
        }

        // Add positive button
        builder.setPositiveButton(R.string.add_template, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocListener.onNewTemplate();
                dismiss();
            }
        });
    }

    // --------------------
    // Methods - Actions
    // --------------------

    private void edit(Card template) {
    }

    private void delete(Card template) {
    }

    // --------------------
    // Callback interfaces
    // --------------------

    public interface OnCompleteListener {
        void onNewTemplate();
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