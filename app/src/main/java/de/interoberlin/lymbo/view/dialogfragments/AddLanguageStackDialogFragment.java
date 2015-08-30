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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.translate.Language;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class AddLanguageStackDialogFragment extends DialogFragment {
    private EditText etTitle;
    private EditText etSubtitle;
    private EditText etAuthor;
    private TextView tvLanguageFrom;
    private TextView tvLanguageTo;
    private TableLayout tblLanguagesFrom;
    private TableLayout tblLanguagesTo;

    private List<CheckBox> checkboxesLanguageFrom = new ArrayList<>();
    private List<String> languagesFrom = new ArrayList<>();

    private List<CheckBox> checkboxesLanguageTo = new ArrayList<>();
    private List<String> languagesTo = new ArrayList<>();

    private OnCompleteListener ocListener;

    // --------------------
    // Constructors
    // --------------------

    public AddLanguageStackDialogFragment() {
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        final View v = View.inflate(getActivity(), R.layout.dialogfragment_add_language_stack, null);

        etTitle = (EditText) v.findViewById(R.id.etTitle);
        etSubtitle = (EditText) v.findViewById(R.id.etSubtitle);
        etAuthor = (EditText) v.findViewById(R.id.etAuthor);
        tvLanguageFrom = (TextView) v.findViewById(R.id.tvLanguageFrom);
        tvLanguageTo = (TextView) v.findViewById(R.id.tvLanguageTo);
        tblLanguagesFrom = (TableLayout) v.findViewById(R.id.tblLanguagesFrom);
        tblLanguagesTo = (TableLayout) v.findViewById(R.id.tblLanguagesTo);

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

        AlertDialog dialog = (AlertDialog) getDialog();

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
                    }

                    if (languageFrom == null) {
                        tvLanguageFrom.setError(getActivity().getResources().getString(R.string.select_a_language), dWarning);
                    }

                    if (languageTo == null) {
                        tvLanguageTo.setError(getActivity().getResources().getString(R.string.select_a_language), dWarning);
                    }

                    if (!title.isEmpty() && languageFrom != null&& languageTo != null)  {
                        ocListener.onAddLanguageStack(title, subtitle, author, languageFrom, languageTo);
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
        void onAddLanguageStack(String title, String subtitle, String author, Language languageFrom, Language languageTo);
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