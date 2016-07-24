package de.interoberlin.lymbo.view.dialogs;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;

public class TemplateDialog extends DialogFragment {
    // <editor-fold defaultstate="expanded" desc="Members">

    public static final String TAG = TemplateDialog.class.getSimpleName();

    /// View
    @BindView(R.id.etTitle) EditText etTitle;
    @BindView(R.id.etFront) EditText etFront;
    @BindView(R.id.tblTextFront) TableLayout tblTextFront;
    @BindView(R.id.ivAddTextFront) ImageView ivAddTextFront;
    @BindView(R.id.etBack) EditText etBack;
    @BindView(R.id.tblTextBack) TableLayout tblTextBack;
    @BindView(R.id.ivAddTextBack) ImageView ivAddTextBack;
    @BindView(R.id.tblTags) TableLayout tblTags;
    @BindView(R.id.ivAddTag) ImageView ivAddTag;

    private OnCompleteListener ocListener;

    // </editor-fold>

    // --------------------
    // Methods - Lifecycle
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Lifecycle">

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Resources res = getActivity().getResources();

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialog_template, null);
        ButterKnife.bind(this, v);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String dialogTitle = bundle.getString(res.getString(R.string.bundle_dialog_title));
        final String title = bundle.getString(res.getString(R.string.bundle_title));
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

        if (title != null)
            etTitle.setText(title);

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

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString().trim();

                Drawable dWarning = ContextCompat.getDrawable(getActivity(), R.drawable.ic_warning_black_48dp);

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

    // </editor-fold>

    // --------------------
    // Methods - Actions
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Actions">

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

    // </editor-fold>

    // --------------------
    // Methos
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Methods">

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
            if (t.getValue().equalsIgnoreCase(tag.getValue()))
                return true;
        }

        return false;
    }

    // </editor-fold>

    // --------------------
    // Callback interfaces
    // --------------------

    // <editor-fold defaultstate="expanded" desc="Callback interfaces">

    public interface OnCompleteListener {
        void onAddTemplate(String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags);

        void onEditTemplate(String uuid, String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags);
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