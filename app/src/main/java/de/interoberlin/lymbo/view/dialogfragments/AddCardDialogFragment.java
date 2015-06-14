package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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
    CardsController cardsController = CardsController.getInstance();

    // Model
    private List<Tag> tags;

    // Views
    private EditText etFront;
    private EditText etBack;
    private LinearLayout llAddTags;
    private TableLayout tblTags;
    private ImageView ivAdd;

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

        final Context c = getActivity();

        // Load layout
        final View v = View.inflate(c, R.layout.dialogfragment_add_card, null);

        etFront = (EditText) v.findViewById(R.id.etFront);
        etBack = (EditText) v.findViewById(R.id.etBack);
        llAddTags = (LinearLayout) v.findViewById(R.id.llAddTags);
        tblTags = (TableLayout) v.findViewById(R.id.tblTags);
        ivAdd = (ImageView) v.findViewById(R.id.ivAdd);

        llAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addTagsIsExpanded) {
                    addTagsIsExpanded = false;
                    tblTags.startAnimation(ViewUtil.collapse(c, tblTags));
                } else {
                    addTagsIsExpanded = true;
                    tblTags.startAnimation(ViewUtil.expand(c, tblTags));
                }
            }
        });

        if (!addTagsIsExpanded) {
            tags = ModelUtil.copy(cardsController.getLymbo().getTags());
        }

        tblTags.getLayoutParams().height = 0;

        // Existing tags
        for (final Tag t : tags) {
            final TableRow tr = new TableRow(c);

            final CheckBox cb = new CheckBox(c);
            final TextView tvText = new TextView(c);

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

        // Add button
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow trLast = (TableRow) tblTags.getChildAt(tblTags.getChildCount() - 2);

                if (!(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
                    // New tag
                    final TableRow tr = new TableRow(c);
                    final CheckBox cb = new CheckBox(c);
                    final EditText etText = new EditText(c);
                    tr.addView(cb);
                    tr.addView(etText);
                    etText.setHint(R.string.new_tag);
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
                String front = etFront.getText().toString().trim();

                Drawable dWarning = c.getResources().getDrawable(R.drawable.ic_action_warning);
                boolean valid = true;

                if (front.isEmpty()) {
                    etFront.setError(c.getResources().getString(R.string.field_must_not_be_empty), dWarning);
                    valid = false;
                }

                if (valid) {
                    ocListener.onAddSimpleCard(etFront.getText().toString(), etBack.getText().toString(), getSelectedTags(tags, tblTags));
                    dismiss();
                }
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
    // Methods
    // --------------------

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

                if (trLast.getChildAt(0) instanceof CheckBox && ((CheckBox) trLast.getChildAt(0)).isChecked() && trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty()) {
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
        void onAddSimpleCard(String frontText, String backText, List<Tag> tags);
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