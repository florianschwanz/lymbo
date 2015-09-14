package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.Displayable;
import de.interoberlin.lymbo.model.card.enums.ChoiceType;
import de.interoberlin.lymbo.util.Configuration;

public class ChoiceComponent implements Displayable {
    private ChoiceType type = ChoiceType.MULTIPLE;
    private List<Answer> answers = new ArrayList<>();

    private List<CheckBox> checkboxes = new ArrayList<>();

    // --------------------
    // Constructor
    // --------------------

    public ChoiceComponent() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        final TableLayout tlChoices = (TableLayout) li.inflate(R.layout.component_choice, parent, false);

        for (final Answer answer : answers) {

            LinearLayout llAnswer = (LinearLayout) li.inflate(R.layout.component_answer, parent, false);

            final CheckBox cb = (CheckBox) llAnswer.findViewById(R.id.cb);
            final TextView tvText = (TextView) llAnswer.findViewById(R.id.tvText);

            checkboxes.add(cb);

            cb.setChecked(answer.isSelected());
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (getChoiceType()) {
                        case MULTIPLE: {
                            answer.setSelected(b);
                            break;
                        }
                        case SINGLE: {
                            answer.setSelected(b);
                            if (b) {
                                answer.setSelected(b);
                                for (CheckBox c : checkboxes) {
                                    c.setChecked(false);
                                }

                                cb.setChecked(true);
                            }

                            break;
                        }
                    }
                }
            });

            if (answer.getTranslations().containsKey(Configuration.getLanguage(c)))
                tvText.setText(answer.getTranslations().get(Configuration.getLanguage(c)));
            else
                tvText.setText(answer.getValue());

            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (getChoiceType()) {
                        case MULTIPLE: {
                            cb.toggle();
                            break;
                        }
                        case SINGLE: {
                            if (!cb.isChecked()) {
                                for (CheckBox c : checkboxes) {
                                    c.setChecked(false);
                                }

                                cb.setChecked(true);
                            }

                            break;
                        }
                    }
                }
            });


            tlChoices.addView(llAnswer);

        }

        return tlChoices;
    }

    @Override
    public View getEditableView(Context c, final Activity a, ViewGroup parent) {
        return new View(c);
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public ChoiceType getChoiceType() {
        return type;
    }

    public void setChoiceType(ChoiceType type) {
        this.type = type;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
