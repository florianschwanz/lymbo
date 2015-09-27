package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.lymbo.model.card.Tag;

public class TemplateDialogFragment extends DialogFragment {
    private OnCompleteListener ocListener;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardsController cardsController = CardsController.getInstance(getActivity());
        final Stack stack = cardsController.getStack();
        final Resources res = getActivity().getResources();

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_template, null);

        final EditText etFront = (EditText) v.findViewById(R.id.etFront);
        final TableLayout tblTextFront = (TableLayout) v.findViewById(R.id.tblTextFront);
        final ImageView ivAddTextFront = (ImageView) v.findViewById(R.id.ivAddTextFront);

        final EditText etBack = (EditText) v.findViewById(R.id.etBack);
        final TableLayout tblTextBack = (TableLayout) v.findViewById(R.id.tblTextBack);
        final ImageView ivAddTextBack = (ImageView) v.findViewById(R.id.ivAddTextBack);

        final TableLayout tblTags = (TableLayout) v.findViewById(R.id.tblTags);
        final ImageView ivAddTag = (ImageView) v.findViewById(R.id.ivAddTag);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String dialogTitle = bundle.getString(res.getString(R.string.bundle_dialog_title));
        final String frontTitle = bundle.getString(res.getString(R.string.bundle_front_title));
        final String backTitle = bundle.getString(res.getString(R.string.bundle_back_title));
        final ArrayList<String> textsFront = bundle.getStringArrayList(res.getString(R.string.bundle_texts_front));
        final ArrayList<String> textsBack = bundle.getStringArrayList(res.getString(R.string.bundle_texts_back));
        final ArrayList<String> tagsAll = bundle.getStringArrayList(res.getString(R.string.bundle_tags_all));
        final ArrayList<String> tagsSelected = bundle.getStringArrayList(res.getString(R.string.bundle_tags_selected));

        // Fill views with arguments
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(dialogTitle);

        if (frontTitle != null)
            etFront.setText(frontTitle);

        if (backTitle != null)
            etBack.setText(backTitle);

        if (textsFront != null) {
            for (String s : textsFront) {
                final TableRow tr = new TableRow(getActivity());
                final EditText etText = new EditText(getActivity());
                tr.addView(etText);
                etText.setText(s);
                tblTextFront.addView(tr, tblTextFront.getChildCount());
            }
        }

        if (textsBack != null) {
            for (String s : textsBack) {
                final TableRow tr = new TableRow(getActivity());
                final EditText etText = new EditText(getActivity());
                tr.addView(etText);
                etText.setText(s);
                tblTextBack.addView(tr, tblTextBack.getChildCount());
            }
        }
        if (tagsAll != null) {
            for (final String tag : tagsAll) {
                if (tag != null && !tag.equals(getActivity().getResources().getString(R.string.no_tag))) {
                    final TableRow tr = new TableRow(getActivity());
                    final CheckBox cb = new CheckBox(getActivity());
                    final TextView tvText = new TextView(getActivity());

                    tr.addView(cb);
                    tr.addView(tvText);

                    if (tagsSelected != null && tagsSelected.contains(tag))
                        cb.setChecked(true);

                    tvText.setText(tag);
                    tvText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cb.toggle();
                        }
                    });

                    tblTags.addView(tr, tblTags.getChildCount());
                }
            }
        }

        // Add actions
        ivAddTextFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addText(tblTextFront);
            }
        });

        ivAddTextBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addText(tblTextBack);
            }
        });

        ivAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTag(tblTags);
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

        // Get arguments
        Bundle bundle = this.getArguments();
        final String templateUuid = bundle.getString(getActivity().getResources().getString(R.string.bundle_template_uuid));

        AlertDialog dialog = (AlertDialog) getDialog();
        final EditText etTitle = (EditText) dialog.findViewById(R.id.etTitle);
        final EditText etFront = (EditText) dialog.findViewById(R.id.etFront);
        final TableLayout tblTextFront = (TableLayout) dialog.findViewById(R.id.tblTextFront);
        final EditText etBack = (EditText) dialog.findViewById(R.id.etBack);
        final TableLayout tblTextBack = (TableLayout) dialog.findViewById(R.id.tblTextBack);
        final TableLayout tblTags = (TableLayout) dialog.findViewById(R.id.tblTags);

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString().trim();

                Drawable dWarning = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_warning);

                if (title.isEmpty()) {
                    etTitle.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                } else {
                    if (templateUuid == null) {
                        ocListener.onAddTemplate(etTitle.getText().toString(), etFront.getText().toString(), getTexts(tblTextFront), etBack.getText().toString(), getTexts(tblTextBack), getSelectedTags(tblTags));
                    } else {
                        ocListener.onEditTemplate(templateUuid, etTitle.getText().toString(), etFront.getText().toString(), getTexts(tblTextFront), etBack.getText().toString(), getTexts(tblTextBack), getSelectedTags(tblTags));
                    }
                    dismiss();
                }
            }
        });
    }

    // --------------------
    // Methods - Actions
    // --------------------

    private void addText(TableLayout tblText) {
        TableRow row = (TableRow) tblText.getChildAt(tblText.getChildCount() - 1);

        if (row == null || row.getChildCount() < 1 || !(row.getChildAt(0) instanceof EditText) || (row.getChildAt(0) instanceof EditText && !((EditText) row.getChildAt(0)).getText().toString().isEmpty())) {
            final TableRow tr = new TableRow(getActivity());
            final EditText etText = new EditText(getActivity());
            tr.addView(etText);
            etText.setHint(R.string.new_text);
            etText.requestFocus();
            tblText.addView(tr, tblText.getChildCount());
        }
    }

    private void addTag(TableLayout tblTags) {
        TableRow trLast = (TableRow) tblTags.getChildAt(tblTags.getChildCount() - 1);

        if (trLast == null || trLast.getChildCount() < 2 || !(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
            final TableRow tr = new TableRow(getActivity());
            final CheckBox cb = new CheckBox(getActivity());
            final EditText etText = new EditText(getActivity());
            tr.addView(cb);
            tr.addView(etText);
            etText.setHint(R.string.new_tag);
            etText.requestFocus();
            cb.setChecked(true);
            tblTags.addView(tr, tblTags.getChildCount());
        }
    }

    // --------------------
    // Methods
    // --------------------

    private List<String> getTexts(TableLayout tblTexts) {
        List<String> texts = new ArrayList<>();

        for (int i = 0; i < tblTexts.getChildCount(); i++) {
            if (tblTexts.getChildAt(i) instanceof TableRow) {
                TableRow row = (TableRow) tblTexts.getChildAt(i);

                if (row.getChildCount() > 0 && row.getChildAt(0) instanceof EditText && !((EditText) row.getChildAt(0)).getText().toString().isEmpty()) {
                    texts.add(((EditText) row.getChildAt(0)).getText().toString());
                }
            }
        }

        return texts;
    }

    private List<Tag> getSelectedTags(TableLayout tblTags) {
        List<Tag> selectedTags = new ArrayList<>();

        for (int i = 0; i < tblTags.getChildCount(); i++) {
            if (tblTags.getChildAt(i) instanceof TableRow) {
                TableRow row = (TableRow) tblTags.getChildAt(i);
                Tag tag = null;
                if (row.getChildCount() > 1 && row.getChildAt(0) instanceof CheckBox && ((CheckBox) row.getChildAt(0)).isChecked()) {
                    if (row.getChildAt(1) instanceof EditText && !((EditText) row.getChildAt(1)).getText().toString().isEmpty()) {
                        tag = new Tag(((EditText) row.getChildAt(1)).getText().toString());
                    } else if (row.getChildAt(1) instanceof TextView && !((TextView) row.getChildAt(1)).getText().toString().isEmpty()) {
                        tag = new Tag(((TextView) row.getChildAt(1)).getText().toString());
                    }

                    if (tag != null && !containsTag(selectedTags, tag)) {
                        selectedTags.add(tag);
                    }
                }
            }
        }

        return selectedTags;
    }

    private boolean containsTag(List<Tag> tags, Tag tag) {
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(tag.getName()))
                return true;
        }

        return false;
    }

    // --------------------
    // Callback interfaces
    // --------------------

    public interface OnCompleteListener {
        void onAddTemplate(String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags);
        void onEditTemplate(String uuid, String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags);
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