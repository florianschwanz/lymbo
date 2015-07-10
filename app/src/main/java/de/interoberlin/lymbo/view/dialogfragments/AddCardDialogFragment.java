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
import android.widget.CompoundButton;
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

public class AddCardDialogFragment extends DialogFragment {
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
    // private ImageView ivExpandTags;
    private TableLayout tblTags;
    private ImageView ivAddTag;

    private boolean addTextFrontIsExpanded = false;
    private boolean addTextBackIsExpanded = false;
    private boolean addTagsIsExpanded = false;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public AddCardDialogFragment() {
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

        // Add actions
        ivExpandTextsFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addTextFrontIsExpanded) {
                    addTextFrontIsExpanded = false;
                    tblTextFront.startAnimation(ViewUtil.collapse(getActivity(), tblTextFront));
                } else {
                    addTextFrontIsExpanded = true;
                    tblTextFront.startAnimation(ViewUtil.expand(getActivity(), tblTextFront));
                }
            }
        });

        ivExpandTextsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addTextBackIsExpanded) {
                    addTextBackIsExpanded = false;
                    tblTextBack.startAnimation(ViewUtil.collapse(getActivity(), tblTextBack));
                } else {
                    addTextBackIsExpanded = true;
                    tblTextBack.startAnimation(ViewUtil.expand(getActivity(), tblTextBack));
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

        tblTextFront.getLayoutParams().height = 0;
        tblTextBack.getLayoutParams().height = 0;
        tblTags.getLayoutParams().height = 0;

        // Existing tags
        for (final Tag t : tags) {
            if (!t.getName().equals(getActivity().getResources().getString(R.string.no_tag))) {
                final TableRow tr = new TableRow(getActivity());

                final CheckBox cb = new CheckBox(getActivity());
                final TextView tvText = new TextView(getActivity());

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

                tblTags.addView(tr, tblTags.getChildCount() - 1);
            }
        }

        ivAddTextFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow trLast = (TableRow) tblTextFront.getChildAt(tblTextFront.getChildCount() - 2);

                if (trLast == null || trLast.getChildCount() < 2 || !(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
                    // New tag
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
                TableRow trLast = (TableRow) tblTextBack.getChildAt(tblTextBack.getChildCount() - 2);

                if (trLast == null || trLast.getChildCount() < 2 || !(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
                    // New tag
                    final TableRow tr = new TableRow(getActivity());
                    final EditText etText = new EditText(getActivity());
                    tr.addView(etText);
                    etText.setHint(R.string.new_text);
                    etText.requestFocus();
                    tblTextBack.addView(tr, tblTextBack.getChildCount() - 1);
                }
            }
        });

        ivAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow trLast = (TableRow) tblTags.getChildAt(tblTags.getChildCount() - 2);

                if (trLast == null || trLast.getChildCount() < 2 || !(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
                    // New tag
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

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.add_card);

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

        AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String front = etFront.getText().toString().trim();

                    Drawable dWarning = getActivity().getResources().getDrawable(R.drawable.ic_action_warning);

                    if (front.isEmpty()) {
                        etFront.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                    } else {
                        ocListener.onAddSimpleCard(etFront.getText().toString(), getTexts(tblTextFront), etBack.getText().toString(), getTexts(tblTextBack), getSelectedTags(tags, tblTags));
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

                if (row.getChildCount() > 1 && row.getChildAt(0) instanceof CheckBox && ((CheckBox) row.getChildAt(0)).isChecked() && row.getChildAt(1) instanceof EditText && !((EditText) row.getChildAt(1)).getText().toString().isEmpty()) {
                    texts.add(((EditText) row.getChildAt(1)).getText().toString());
                }
            }
        }

        return texts;
    }

    private List<Tag> getSelectedTags(List<Tag> tags, TableLayout tblTags) {
        List<Tag> selectedTags = new ArrayList<>();

        // Existing
        for (Tag t : tags) {
            if (t.isChecked()) {
                selectedTags.add(t);
            }
        }

        // Newly added
        for (int i = 0; i < tblTags.getChildCount(); i++) {
            if (tblTags.getChildAt(i) instanceof TableRow) {
                TableRow trLast = (TableRow) tblTags.getChildAt(i);

                if (trLast.getChildCount() > 1 && trLast.getChildAt(0) instanceof CheckBox && ((CheckBox) trLast.getChildAt(0)).isChecked() && trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty()) {
                    Tag tag = new Tag(((EditText) trLast.getChildAt(1)).getText().toString());

                    if (!containsTag(selectedTags, tag)) {
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
        void onAddSimpleCard(String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags);
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