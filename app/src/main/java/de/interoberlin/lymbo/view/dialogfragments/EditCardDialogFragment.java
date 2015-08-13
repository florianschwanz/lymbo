package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.CardsController;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.util.ModelUtil;
import de.interoberlin.lymbo.util.ViewUtil;

public class EditCardDialogFragment extends DialogFragment {
    // Controllers
    CardsController cardsController;

    // Model
    private List<Tag> tags;

    // Views
    private EditText etFront;
    private ImageView ivExpandTextsFront;
    private TableLayout tblTextFront;
    private ImageView ivAddTextFront;

    private EditText etBack;
    private ImageView ivExpandTextsBack;
    private TableLayout tblTextBack;
    private ImageView ivAddTextBack;

    private LinearLayout llAddTags;
    private TableLayout tblTags;
    private ImageView ivAddTag;

    private boolean addTextFrontIsExpanded = false;
    private boolean addTextBackIsExpanded = false;
    private boolean addTagsIsExpanded = false;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public EditCardDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardsController = CardsController.getInstance(getActivity());

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_add_card, null);

        etFront = (EditText) v.findViewById(R.id.etFront);
        ivExpandTextsFront = (ImageView) v.findViewById(R.id.ivExpandTextsFront);
        tblTextFront = (TableLayout) v.findViewById(R.id.tblTextFront);
        ivAddTextFront = (ImageView) v.findViewById(R.id.ivAddTextFront);

        etBack = (EditText) v.findViewById(R.id.etBack);
        ivExpandTextsBack = (ImageView) v.findViewById(R.id.ivExpandTextsBack);
        tblTextBack = (TableLayout) v.findViewById(R.id.tblTextBack);
        ivAddTextBack = (ImageView) v.findViewById(R.id.ivAddTextBack);

        llAddTags = (LinearLayout) v.findViewById(R.id.llAddTags);
        tblTags = (TableLayout) v.findViewById(R.id.tblTags);
        ivAddTag = (ImageView) v.findViewById(R.id.ivAddTag);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String frontTitle = bundle.getString(getActivity().getResources().getString(R.string.bundle_front_title));
        String backTitle = bundle.getString(getActivity().getResources().getString(R.string.bundle_back_title));
        ArrayList<String> textsFront = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_texts_front));
        ArrayList<String> textsBack = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_texts_back));
        ArrayList<String> tagsLymbo = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_tags_lymbo));
        ArrayList<String> tagsCard = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_tags_card));

        if (frontTitle != null)
            etFront.setText(frontTitle);
        if (backTitle != null)
            etBack.setText(backTitle);

        // Add actions
        ivExpandTextsFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addTextFrontIsExpanded) {
                    addTextFrontIsExpanded = false;
                    ivExpandTextsFront.setImageResource(R.drawable.ic_action_expand);
                    tblTextFront.startAnimation(ViewUtil.collapse(getActivity(), tblTextFront));
                } else {
                    addTextFrontIsExpanded = true;
                    ivExpandTextsFront.setImageResource(R.drawable.ic_action_collapse);
                    tblTextFront.startAnimation(ViewUtil.expand(getActivity(), tblTextFront));
                }
            }
        });

        ivExpandTextsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addTextBackIsExpanded) {
                    addTextBackIsExpanded = false;
                    ivExpandTextsBack.setImageResource(R.drawable.ic_action_expand);
                    tblTextBack.startAnimation(ViewUtil.collapse(getActivity(), tblTextBack));
                } else {
                    addTextBackIsExpanded = true;
                    ivExpandTextsBack.setImageResource(R.drawable.ic_action_collapse);
                    tblTextBack.startAnimation(ViewUtil.expand(getActivity(), tblTextBack));
                }
            }
        });

        ivAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) tblTags.getChildAt(tblTags.getChildCount() - 2);

                if (row == null || row.getChildCount() < 2 || !(row.getChildAt(1) instanceof EditText) || (row.getChildAt(1) instanceof EditText && !((EditText) row.getChildAt(1)).getText().toString().isEmpty())) {
                    final TableRow tr = new TableRow(getActivity());
                    final CheckBox cb = new CheckBox(getActivity());
                    final EditText etText = new EditText(getActivity());
                    tr.addView(cb);
                    tr.addView(etText);
                    etText.setHint(R.string.new_tag);
                    etText.requestFocus();
                    cb.setChecked(true);
                    tblTags.addView(tr, tblTags.getChildCount() - 1);
                }
            }
        });

        tblTextFront.getLayoutParams().height = 0;
        tblTextBack.getLayoutParams().height = 0;
        tblTags.getLayoutParams().height = 0;

        // Existing texts front
        if (textsFront != null) {
            for (final String textFrontValue : textsFront) {
                final TableRow tr = new TableRow(getActivity());
                final EditText etText = new EditText(getActivity());
                tr.addView(etText);
                etText.setText(textFrontValue);

                tblTextFront.addView(tr, tblTextFront.getChildCount() - 1);
            }
        }

        // Existing texts back
        if (textsBack != null) {
            for (final String textBackValue : textsBack) {
                final TableRow tr = new TableRow(getActivity());
                final EditText etText = new EditText(getActivity());
                tr.addView(etText);
                etText.setText(textBackValue);

                tblTextBack.addView(tr, tblTextBack.getChildCount() - 1);
            }
        }

        ivAddTextFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) tblTextFront.getChildAt(tblTextFront.getChildCount() - 2);

                if (row == null || row.getChildCount() < 1 || !(row.getChildAt(0) instanceof EditText) || (row.getChildAt(0) instanceof EditText && !((EditText) row.getChildAt(0)).getText().toString().isEmpty())) {
                    final TableRow tr = new TableRow(getActivity());
                    final EditText etText = new EditText(getActivity());
                    tr.addView(etText);
                    etText.setHint(R.string.new_text);
                    etText.requestFocus();
                    tblTextFront.addView(tr, tblTextFront.getChildCount() - 1);
                }
            }
        });

        ivAddTextBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) tblTextBack.getChildAt(tblTextBack.getChildCount() - 2);

                if (row == null || row.getChildCount() < 1 || !(row.getChildAt(0) instanceof EditText) || (row.getChildAt(0) instanceof EditText && !((EditText) row.getChildAt(0)).getText().toString().isEmpty())) {
                    final TableRow tr = new TableRow(getActivity());
                    final EditText etText = new EditText(getActivity());
                    tr.addView(etText);
                    etText.setHint(R.string.new_text);
                    etText.requestFocus();
                    tblTextBack.addView(tr, tblTextBack.getChildCount() - 1);
                }
            }
        });

        llAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addTagsIsExpanded) {
                    addTagsIsExpanded = false;
                    tblTags.startAnimation(ViewUtil.collapse(getActivity(), tblTags));
                } else {
                    addTagsIsExpanded = true;
                    tblTags.startAnimation(ViewUtil.expand(getActivity(), tblTags));
                }
            }
        });

        if (!addTagsIsExpanded) {
            tags = ModelUtil.copy(cardsController.getLymbo().getTags());
        }

        // Existing tags
        for (final String tag : tagsLymbo) {
            if (!tag.equals(getActivity().getResources().getString(R.string.no_tag))) {
                final TableRow tr = new TableRow(getActivity());

                final CheckBox cb = new CheckBox(getActivity());
                final TextView tvText = new TextView(getActivity());

                tr.addView(cb);
                tr.addView(tvText);

                if (tagsCard.contains(tag))
                    cb.setChecked(true);

                tvText.setText(tag);
                tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb.toggle();
                    }
                });

                tblTags.addView(tr, tblTags.getChildCount() - 1);
            }
        }

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.edit_card);

        // Add positive button
        builder.setPositiveButton(R.string.okay, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    public void onStart() {
        super.onStart();

        // Get arguments
        Bundle bundle = this.getArguments();
        final String cardUuid = bundle.getString(getActivity().getResources().getString(R.string.bundle_card_uuid));

        AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String frontTitleValue = etFront.getText().toString().trim();
                    List<String> frontTextValues = getTexts(tblTextFront);
                    String backTitleValue = etBack.getText().toString().trim();
                    List<String> backTextValues = getTexts(tblTextBack);
                    List<Tag> tags = getSelectedTags(tblTags);

                    Drawable dWarning = getActivity().getResources().getDrawable(R.drawable.ic_action_warning);
                    boolean valid = true;

                    if (frontTitleValue.isEmpty()) {
                        etFront.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                        valid = false;
                    }

                    if (valid) {
                        ocListener.onEditSimpleCard(cardUuid, frontTitleValue, frontTextValues, backTitleValue, backTextValues, tags);
                        dismiss();
                    }
                }
            });
        }
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
    // Methods
    // --------------------

    private List<String> getTexts(TableLayout tblTexts) {
        List<String> texts = new ArrayList<>();

        for (int i = 0; i < tblTexts.getChildCount(); i++) {
            if (tblTexts.getChildAt(i) instanceof TableRow) {
                TableRow row = (TableRow) tblTexts.getChildAt(i);

                if (row.getChildCount() > 0  && row.getChildAt(0) instanceof EditText && !((EditText) row.getChildAt(0)).getText().toString().isEmpty()) {
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
        void onEditSimpleCard(String uuid, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags);
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