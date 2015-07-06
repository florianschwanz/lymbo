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
    private EditText etBack;
    private LinearLayout llAddTags;
    private TableLayout tblTags;
    private ImageView ivAdd;

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

        final Context c = getActivity();

        // Load layout
        final View v = View.inflate(c, R.layout.dialogfragment_add_card, null);
        etFront = (EditText) v.findViewById(R.id.etFront);
        etBack = (EditText) v.findViewById(R.id.etBack);
        llAddTags = (LinearLayout) v.findViewById(R.id.llAddTags);
        tblTags = (TableLayout) v.findViewById(R.id.tblTags);
        ivAdd = (ImageView) v.findViewById(R.id.ivAdd);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String uuid = bundle.getString(c.getResources().getString(R.string.bundle_uuid));
        final int pos = bundle.getInt(c.getResources().getString(R.string.bundle_pos));
        final String frontTitle = bundle.getString(c.getResources().getString(R.string.bundle_front_title));
        String backTitle = bundle.getString(c.getResources().getString(R.string.bundle_back_title));
        ArrayList<String> tagsLymbo = bundle.getStringArrayList(c.getResources().getString(R.string.bundle_tags_lymbo));
        ArrayList<String> tagsCard = bundle.getStringArrayList(c.getResources().getString(R.string.bundle_tags_card));

        if (frontTitle != null)
            etFront.setText(frontTitle);
        if (backTitle != null)
            etBack.setText(backTitle);

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
        for (final String tag : tagsLymbo) {
            if (!tag.equals(getActivity().getResources().getString(R.string.no_tag))) {
                final TableRow tr = new TableRow(c);

                final CheckBox cb = new CheckBox(c);
                final TextView tvText = new TextView(c);

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

        // Add button
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow trLast = (TableRow) tblTags.getChildAt(tblTags.getChildCount() - 2);

                if (trLast == null || trLast.getChildCount() < 2 || !(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
                    // New tag
                    final TableRow tr = new TableRow(c);
                    final CheckBox cb = new CheckBox(c);
                    final EditText etText = new EditText(c);
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
                    ocListener.onEditSimpleCard(uuid, pos, etFront.getText().toString(), etBack.getText().toString(), getSelectedTags(tblTags));
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
        void onEditSimpleCard(String uuid, int pos, String frontText, String backText, List<Tag> tags);
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