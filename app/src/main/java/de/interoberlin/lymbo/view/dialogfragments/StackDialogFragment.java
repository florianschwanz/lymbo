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
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.translate.Language;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class StackDialogFragment extends DialogFragment {
    private List<CheckBox> checkboxesLanguageFrom = new ArrayList<>();
    private List<String> languagesFrom = new ArrayList<>();

    private List<CheckBox> checkboxesLanguageTo = new ArrayList<>();
    private List<String> languagesTo = new ArrayList<>();

    private boolean addLanguagesIsExpanded = false;
    private boolean addCategoriesIsExpanded = false;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public StackDialogFragment() {
    }

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
        final EditText etAuthor = (EditText) v.findViewById(R.id.etAuthor);

        final LinearLayout llAddLanguages = (LinearLayout) v.findViewById(R.id.llAddLanguages);
        final LinearLayout llLanguages = (LinearLayout) v.findViewById(R.id.llLanguages);
        final TableLayout tblLanguagesFrom = (TableLayout) v.findViewById(R.id.tblLanguagesFrom);
        final TableLayout tblLanguagesTo = (TableLayout) v.findViewById(R.id.tblLanguagesTo);

        final LinearLayout llAddCategories = (LinearLayout) v.findViewById(R.id.llAddCategories);
        final TableLayout tblCategories = (TableLayout) v.findViewById(R.id.tblCategories);
        final ImageView ivAddCategory = (ImageView) v.findViewById(R.id.ivAddCategory);

        // Get arguments
        Bundle bundle = this.getArguments();
        final String title = bundle.getString(getActivity().getResources().getString(R.string.bundle_title));
        final String subtitle = bundle.getString(getActivity().getResources().getString(R.string.bundle_subtitle));
        final String author = bundle.getString(getActivity().getResources().getString(R.string.bundle_author));
        final Language languageFrom = Language.fromString(bundle.getString(getActivity().getResources().getString(R.string.bundle_language_from)));
        final Language languageTo = Language.fromString(bundle.getString(getActivity().getResources().getString(R.string.bundle_language_to)));
        final ArrayList<String> categoriesLymbo = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_categories_lymbo));
        final ArrayList<String> categoriesAll = bundle.getStringArrayList(getActivity().getResources().getString(R.string.bundle_categories_all));

        // Fill views with arguments
        if (title != null)
            etTitle.setText(title);
        if (subtitle != null)
            etSubtitle.setText(subtitle);
        if (author != null)
            etAuthor.setText(author);

        for (final Language l : Language.values()) {
            if (l.isActive()) {
                languagesFrom.add(l.getLangCode());

                final TableRow tr = new TableRow(getActivity());
                final CheckBox cb = new CheckBox(getActivity());
                final RobotoTextView tvText = new RobotoTextView(getActivity());

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

        for (final Language l : Language.values()) {
            if (l.isActive()) {
                languagesTo.add(l.getLangCode());

                final TableRow tr = new TableRow(getActivity());
                final CheckBox cb = new CheckBox(getActivity());
                final RobotoTextView tvText = new RobotoTextView(getActivity());

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

        for (final Language l : Language.values()) {
            languagesFrom.add(l.getLangCode());

            final TableRow tr = new TableRow(getActivity());

            final CheckBox cb = new CheckBox(getActivity());
            final RobotoTextView tvText = new RobotoTextView(getActivity());

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

            tvText.setText(l.getName(getActivity()));
            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.toggle();
                }
            });

            tblLanguagesFrom.addView(tr);
        }

        for (final Language l : Language.values()) {
            languagesTo.add(l.getLangCode());

            final TableRow tr = new TableRow(getActivity());

            final CheckBox cb = new CheckBox(getActivity());
            final RobotoTextView tvText = new RobotoTextView(getActivity());

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

            tvText.setText(l.getName(getActivity()));
            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.toggle();
                }
            });

            tblLanguagesTo.addView(tr);
        }

        if (categoriesAll != null) {
            for (final String tag : categoriesAll) {
                if (tag != null && !tag.equals(getActivity().getResources().getString(R.string.no_category))) {
                    final TableRow tr = new TableRow(getActivity());

                    final CheckBox cb = new CheckBox(getActivity());
                    final TextView tvText = new TextView(getActivity());

                    tr.addView(cb);
                    tr.addView(tvText);

                    if (categoriesLymbo != null && categoriesLymbo.contains(tag))
                        cb.setChecked(true);

                    tvText.setText(tag);
                    tvText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cb.toggle();
                        }
                    });

                    tblCategories.addView(tr, tblCategories.getChildCount() - 1);
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

        llAddCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addCategoriesIsExpanded) {
                    addCategoriesIsExpanded = false;
                    tblCategories.startAnimation(ViewUtil.collapse(getActivity(), tblCategories));
                } else {
                    addCategoriesIsExpanded = true;
                    tblCategories.startAnimation(ViewUtil.expand(getActivity(), tblCategories));
                }
            }
        });

        llLanguages.getLayoutParams().height = 0;
        tblCategories.getLayoutParams().height = 0;

        ivAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow trLast = (TableRow) tblCategories.getChildAt(tblCategories.getChildCount() - 2);

                if (trLast == null || trLast.getChildCount() < 2 || !(trLast.getChildAt(1) instanceof EditText) || (trLast.getChildAt(1) instanceof EditText && !((EditText) trLast.getChildAt(1)).getText().toString().isEmpty())) {
                    final TableRow tr = new TableRow(getActivity());
                    final CheckBox cb = new CheckBox(getActivity());
                    final EditText etText = new EditText(getActivity());
                    tr.addView(cb);
                    tr.addView(etText);
                    etText.setHint(R.string.new_tag);
                    etText.requestFocus();
                    cb.setChecked(true);
                    tblCategories.addView(tr, tblCategories.getChildCount() - 1);
                }
            }
        });

        // Load dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.add_stack);

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
        final String lymboUuid = bundle.getString(getActivity().getResources().getString(R.string.bundle_lymbo_uuid));

        AlertDialog dialog = (AlertDialog) getDialog();
        final EditText etTitle = (EditText) dialog.findViewById(R.id.etTitle);
        final EditText etSubtitle = (EditText) dialog.findViewById(R.id.etSubtitle);
        final EditText etAuthor = (EditText) dialog.findViewById(R.id.etAuthor);
        final TableLayout tblCategories = (TableLayout) dialog.findViewById(R.id.tblCategories);

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString().trim();
                String subtitle = etSubtitle.getText().toString().trim();
                String author = etAuthor.getText().toString().trim();
                Language languageFrom = null;
                Language languageTo = null;

                for (int i = 0; i < checkboxesLanguageFrom.size(); i++) {
                    if (checkboxesLanguageFrom.get(i).isChecked()) {
                        languageFrom = Language.fromString(languagesFrom.get(i));
                    }
                }

                for (int i = 0; i < checkboxesLanguageTo.size(); i++) {
                    if (checkboxesLanguageTo.get(i).isChecked()) {
                        languageTo = Language.fromString(languagesTo.get(i));
                    }
                }

                Drawable dWarning = getActivity().getResources().getDrawable(R.drawable.ic_action_warning);

                if (title.isEmpty()) {
                    etTitle.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                }

                if (!title.isEmpty()) {
                    if (lymboUuid == null) {
                        ocListener.onAddStack(title, subtitle, author, languageFrom, languageTo, getSelectedTags(tblCategories));
                    } else {
                        ocListener.onEditStack(lymboUuid, title, subtitle, author, languageFrom, languageTo, getSelectedTags(tblCategories));
                    }

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
        void onAddStack(String title, String subtitle, String author, Language languageFrom, Language languageTo, List<Tag> categories);
        void onEditStack(String uuid, String title, String subtitle, String author, Language languageFrom, Language languageTo, List<Tag> categories);
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