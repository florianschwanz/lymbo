package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Text;
import de.interoberlin.lymbo.core.model.v1.impl.Title;
import de.interoberlin.lymbo.core.model.v1.objects.CardObject;
import de.interoberlin.lymbo.core.model.v1.objects.ComponentObject;
import de.interoberlin.lymbo.core.model.v1.objects.ComponentType;
import de.interoberlin.lymbo.util.TagUtil;

public class TemplatesDialogFragment extends DialogFragment {
    public static final String TAG = "templates";
    private OnCompleteListener ocListener;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardsController cardsController = CardsController.getInstance(getActivity());
        Resources res = getActivity().getResources();

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_templates, null);
        final TableLayout tblTemplates = (TableLayout) v.findViewById(R.id.tblTemplates);

        // Get arguments
        // Bundle bundle = this.getArguments();
        // final ArrayList<String> templates = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_templates));

        final ArrayList<String> templates = new ArrayList<>();
        for (CardObject template : cardsController.getStack().getTemplate()) {
            if (template != null && template.getId() != null) {
                templates.add(template.getId());
            }
        }

        // Fill views with arguments
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.templates);

        for (final String t : templates) {
            final TableRow tr = new TableRow(getActivity());
            final TextView tvText = new TextView(getActivity());

            final Card template = cardsController.getTemplateById(t);

            tr.setPadding(0, (int) res.getDimension(R.dimen.table_row_padding), 0, (int) res.getDimension(R.dimen.table_row_padding));
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
                                    dismiss();
                                    return false;
                                }
                            });
                    contextMenu.add(0, 1, 0, getResources().getString(R.string.delete))
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    delete(template);
                                    dismiss();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    // --------------------
    // Methods - Actions
    // --------------------

    private void add() {
        CardsController cardsController = CardsController.getInstance(getActivity());
        ArrayList<String> tagsAll = TagUtil.getDistinctValues(cardsController.getTagsAll());

        TemplateDialogFragment dialog = new TemplateDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.add_template));
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), TAG);
    }

    private void edit(Card template) {
        CardsController cardsController = CardsController.getInstance(getActivity());
        String uuid = template.getId();
        String title = template.getTitle();
        String frontTitle = ((Title) ((Side) template.getSide().get(0)).getFirst(ComponentType.TITLE)).getValue();
        String backTitle = ((Title) ((Side) template.getSide().get(1)).getFirst(ComponentType.TITLE)).getValue();
        ArrayList<String> frontTexts = new ArrayList<>();
        ArrayList<String> backTexts = new ArrayList<>();
        ArrayList<String> tagsAll = TagUtil.getDistinctValues(cardsController.getTagsAll());
        ArrayList<String> tagsSelected = TagUtil.getDistinctValues(TagUtil.getTagList(template.getTag()));

        for (ComponentObject c : template.getSide().get(0).getComponent()) {
            if (c instanceof Text) {
                frontTexts.add(((Text) c).getValue());
            }
        }

        for (ComponentObject c : template.getSide().get(1).getComponent()) {
            if (c instanceof Text) {
                backTexts.add(((Text) c).getValue());
            }
        }

        TemplateDialogFragment dialog = new TemplateDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.bundle_dialog_title), getResources().getString(R.string.edit_template));
        bundle.putString(getResources().getString(R.string.bundle_template_id), uuid);
        bundle.putString(getResources().getString(R.string.bundle_title), title);
        bundle.putString(getResources().getString(R.string.bundle_front_title), frontTitle);
        bundle.putString(getResources().getString(R.string.bundle_back_title), backTitle);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_texts_front), frontTexts);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_texts_back), backTexts);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_all), tagsAll);
        bundle.putStringArrayList(getResources().getString(R.string.bundle_tags_selected), tagsSelected);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), TemplateDialogFragment.TAG);
    }

    private void delete(Card template) {
        ocListener.onDeleteTemplate(template);
    }

    // --------------------
    // Callback interfaces
    // --------------------

    public interface OnCompleteListener {
        void onDeleteTemplate(Card template);
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