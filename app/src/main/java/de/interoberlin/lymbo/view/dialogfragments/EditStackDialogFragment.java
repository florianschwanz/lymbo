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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.translate.Language;
import de.interoberlin.lymbo.util.ViewUtil;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class EditStackDialogFragment extends DialogFragment {
    private List<CheckBox> checkboxesLanguageFrom = new ArrayList<>();
    private List<String> languagesFrom = new ArrayList<>();

    private List<CheckBox> checkboxesLanguageTo = new ArrayList<>();
    private List<String> languagesTo = new ArrayList<>();

    private boolean addLanguagesIsExpanded = false;

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public EditStackDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        Bundle bundle = this.getArguments();
        String title = bundle.getString(getActivity().getResources().getString(R.string.bundle_title));
        String subtitle = bundle.getString(getActivity().getResources().getString(R.string.bundle_subtitle));
        String author = bundle.getString(getActivity().getResources().getString(R.string.bundle_author));
        Language languageFrom = Language.fromString(bundle.getString(getActivity().getResources().getString(R.string.bundle_language_from)));
        Language languageTo = Language.fromString(bundle.getString(getActivity().getResources().getString(R.string.bundle_language_to)));

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_stack, null);

        final EditText etTitle = (EditText) v.findViewById(R.id.etTitle);
        final EditText  etSubtitle = (EditText) v.findViewById(R.id.etSubtitle);
        final EditText etAuthor = (EditText) v.findViewById(R.id.etAuthor);
        final LinearLayout llAddLanguages = (LinearLayout) v.findViewById(R.id.llAddLanguages);
        final LinearLayout llLanguages = (LinearLayout) v.findViewById(R.id.llLanguages);
        final TableLayout tblLanguagesFrom = (TableLayout) v.findViewById(R.id.tblLanguagesFrom);
        final TableLayout tblLanguagesTo = (TableLayout) v.findViewById(R.id.tblLanguagesTo);

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

        llLanguages.getLayoutParams().height = 0;

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

        if (title != null)
            etTitle.setText(title);
        if (subtitle != null)
            etSubtitle.setText(subtitle);
        if (author != null)
            etAuthor.setText(author);

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

        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = etTitle.getText().toString().trim();
                    String subtitle = etSubtitle.getText().toString().trim();
                    String author = etAuthor.getText().toString().trim();
                    Language languageFrom = null;
                    Language languageTo = null;

                    for (int i = 0; i<checkboxesLanguageFrom.size(); i++) {
                        if (checkboxesLanguageFrom.get(i).isChecked()) {
                            languageFrom = Language.fromString(languagesFrom.get(i));
                        }
                    }

                    for (int i = 0; i<checkboxesLanguageTo.size(); i++) {
                        if (checkboxesLanguageTo.get(i).isChecked()) {
                            languageTo = Language.fromString(languagesTo.get(i));
                        }
                    }

                    Drawable dWarning = getActivity().getResources().getDrawable(R.drawable.ic_action_warning);

                    if (title.isEmpty()) {
                        etTitle.setError(getActivity().getResources().getString(R.string.field_must_not_be_empty), dWarning);
                    } else {
                        ocListener.onEditStack(lymboUuid, title, subtitle, author, languageFrom, languageTo);
                        dismiss();
                    }
                }
            });
        }

    }

    // --------------------
    // Callback interfaces
    // --------------------

    public interface OnCompleteListener {
        void onEditStack(String uuid, String title, String subtitle, String author, Language languageFrom, Language languageTo);
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