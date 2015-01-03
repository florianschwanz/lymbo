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
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.enums.ChoiceType;

public class ChoiceComponent implements Displayable {
    private ChoiceType type = ChoiceType.MULTIPLE;
    private List<Answer> answers = new ArrayList<Answer>();

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
        TableLayout tlChoices = (TableLayout) li.inflate(R.layout.component_choice, null);

        for (final Answer answer : answers) {
            LinearLayout llAnswer = (LinearLayout) li.inflate(R.layout.component_answer, null);

            final CheckBox cb = (CheckBox) llAnswer.findViewById(R.id.cb);
            final TextView tvText = (TextView) llAnswer.findViewById(R.id.tvText);

            cb.setChecked(answer.isSelected());
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    answer.setSelected(b);
                }
            });

            tvText.setText(answer.getValue());
            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.toggle();
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
