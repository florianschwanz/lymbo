package de.interoberlin.lymbo.view.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.model.webservice.translate.ELanguage;
import de.interoberlin.lymbo.util.ViewUtil;

public class StackDialogFragment extends DialogFragment {
    public static final String TAG = "stack";

    private List<CheckBox> checkboxesLanguageFrom = new ArrayList<>();
    private List<String> languagesFrom = new ArrayList<>();

    private List<CheckBox> checkboxesLanguageTo = new ArrayList<>();
    private List<String> languagesTo = new ArrayList<>();

    private boolean addLanguagesIsExpanded = false;
    private boolean addTagsIsExpanded = false;

    private OnCompleteListener ocListener;

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_stack, null);
        final EditText etTitle = (EditText) v.findViewById(R.id.etTitle);
        final EditText etSubtitle = (EditText) v.findViewById(R.id.etSubtitle);
        final TextView tvAuthor = (TextView) v.findViewById(R.id.tvAuthor);
        final LinearLayout llAddLanguages = (LinearLayout) v.findViewById(R.id.llAddLanguages);
        final LinearLayout llLanguages = (LinearLayout) v.findViewById(R.id.llLanguages);
        final TableLayout tblLanguagesFrom = (TableLayout) v.findViewById(R.id.tblLanguagesFrom);
        final TableLayout tblLanguagesTo = (TableLayout) v.findViewById(R.id.tblLanguagesTo);
        final LinearLayout llAddTags = (LinearLayout) v.findViewById(R.id.llAddTags);
        final LinearLayout llTags = (LinearLayout) v.findViewById(R.id.llTags);
        final TableLayout tblTags = (TableLayout) v.findViewById(R.id.tblTags);
        final ImageView ivAddTag = (ImageView) v.findViewById(R.id.ivAddTag);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String dialogTitle = bundle.getString(getResources().getString(R.string.bundle_dialog_title));
        final String title = bundle.getString(getActivity().getResources().getString(R.string.bundle_title));
        final String subtitle = bundle.getString(getActivity().getResources().getString(R.string.bundle_subtitle));
        final String author = bundle.getString(getActivity().getResources().getString(R.string.bundle_author));
        final ELanguage languageFrom = ELanguage.fromString(bundle.getString(getActivity().getResources().getString(R.string.bundle_language_from)));
        final ELanguage languageTo = ELanguage.fromString(bundle.getString(getActivity().getResources().getString(R.string.bundle_language_to)));
        final ArrayList<String> tagsAll = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_tags_all));
        final ArrayList<String> tagsSelected = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_tags_selected));

        // Fill views with arguments
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(dialogTitle);

        if (title != null)
            etTitle.setText(title);

        if (subtitle != null)
            etSubtitle.setText(subtitle);

        if (author != null)
            tvAuthor.setText(author);
        else
            tvAuthor.setText(R.string.no_author_specified);

        for (final ELanguage l : ELanguage.values()) {
            if (l.isActive()) {
                languagesFrom.add(l.getLangCode());

                final TableRow tr = new TableRow(getActivity());
                final CheckBox cb = new CheckBox(getActivity());
                final TextView tvText = new TextView(getActivity());

                tr.addView(cb);
                tr.addView(tvText);
                checkboxesLanguageFrom.add(cb);

                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            for (CheckBox c : checkboxesLanguageFrom) {
                                c.setChecked(false);
                            }

                            cb.setChecked(true);
                        }
                    }
                });

                if (languageFrom == l) {
                    cb.setChecked(true);
                }

                tvText.setText(l.getName(getActivity()));
                tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb.toggle();
                    }
                });

                tblLanguagesFrom.addView(tr);
            }
        }

        for (final ELanguage l : ELanguage.values()) {
            if (l.isActive()) {
                languagesTo.add(l.getLangCode());

                final TableRow tr = new TableRow(getActivity());
                final CheckBox cb = new CheckBox(getActivity());
                final TextView tvText = new TextView(getActivity());

                tr.addView(cb);
                tr.addView(tvText);
                checkboxesLanguageTo.add(cb);

                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            for (CheckBox c : checkboxesLanguageTo) {
                                c.setChecked(false);
                            }

                            cb.setChecked(true);
                        }
                    }
                });

                if (languageTo == l) {
                    cb.setChecked(true);
                }

                tvText.setText(l.getName(getActivity()));
                tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb.toggle();
                    }
                });

                tblLanguagesTo.addView(tr);
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

                    tblTags.addView(tr, tblTags.getChildCount() - 1);
                }
            }
        }

        // Add Actions
        llAddLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addLanguagesIsExpanded) {
                    addLanguagesIsExpanded = false;
                    llLanguages.startAnimation(ViewUtil.collapse(getActivity(), llLanguages));
                } else {
                    addLanguagesIsExpanded = true;
                    llLanguages.startAnimation(ViewUtil.expand(getActivity(), llLanguages));
                }
            }
        });

        llAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addTagsIsExpanded) {
                    addTagsIsExpanded = false;
                    llTags.startAnimation(ViewUtil.collapse(getActivity(), llTags));
                } else {
                    addTagsIsExpanded = true;
                    llTags.startAnimation(ViewUtil.expand(getActivity(), llTags));
                }
            }
        });

        ivAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow trLast = (TableRow) tblTags.getChildAt(tblTags.getChildCount() - 2);

                if (trLast == null || trLast.getChildCount() < 2 || !(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
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

        llLanguages.getLayoutParams().height = 0;
        llTags.getLayoutParams().height = 0;

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
        final String lymboUuid = bundle.getString(getActivity().getResources().getString(R.string.bundle_lymbo_id));

        AlertDialog dialog = (AlertDialog) getDialog();
        final EditText etTitle = (EditText) dialog.findViewById(R.id.etTitle);
        final EditText etSubtitle = (EditText) dialog.findViewById(R.id.etSubtitle);
        final TextView tvAuthor = (TextView) dialog.findViewById(R.id.tvAuthor);
        final TableLayout tblTags = (TableLayout) dialog.findViewById(R.id.tblTags);

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString().trim();
                String subtitle = etSubtitle.getText().toString().trim();
                String author = tvAuthor.getText().toString().trim();
                ELanguage languageFrom = null;
                ELanguage languageTo = null;

                for (int i = 0; i < checkboxesLanguageFrom.size(); i++) {
                    if (checkboxesLanguageFrom.get(i).isChecked())
                        languageFrom = ELanguage.fromString(languagesFrom.get(i));
                }

                for (int i = 0; i < checkboxesLanguageTo.size(); i++) {
                    if (checkboxesLanguageTo.get(i).isChecked())
                        languageTo = ELanguage.fromString(languagesTo.get(i));
                }

                Drawable dWarning = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_warning);

                if (title.isEmpty())
                    etTitle.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                else {
                    if (lymboUuid == null)
                        ocListener.onAddStack(title, subtitle, author, languageFrom, languageTo, getSelectedTags(tblTags));
                    else
                        ocListener.onEditStack(lymboUuid, title, subtitle, author, languageFrom, languageTo, getSelectedTags(tblTags));

                    dismiss();
                }
            }
        });
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

                    if (tag != null && !tag.containedInList(selectedTags)) {
                        selectedTags.add(tag);
                    }
                }
            }
        }

        return selectedTags;
    }

    // --------------------
    // Callback interfaces
    // --------------------

    public interface OnCompleteListener {
        void onAddStack(String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags);

        void onEditStack(String uuid, String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags);
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