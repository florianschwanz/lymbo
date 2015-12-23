package de.interoberlin.lymbo.view.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Answer;
import de.interoberlin.lymbo.core.model.v1.impl.Choice;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.TranslationUtil;

public class ChoiceView extends TableLayout {

    // --------------------
    // Constructors
    // --------------------

    public ChoiceView(Context context) {
        super(context);
    }

    public ChoiceView(Context context, final Choice c) {
        super(context);

        inflate(context, R.layout.component_choice, this);

        final List<Answer> answers = new ArrayList<>();
        final List<CheckBox> checkboxes = new ArrayList<>();

        for (final Answer answer : answers) {
            LayoutInflater li = LayoutInflater.from(context);
            LinearLayout llAnswer = (LinearLayout) li.inflate(R.layout.component_answer, this, false);

            final CheckBox cb = (CheckBox) llAnswer.findViewById(R.id.cb);
            final TextView tvText = (TextView) llAnswer.findViewById(R.id.tvText);

            checkboxes.add(cb);

            cb.setChecked(answer.isSelected());
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (c.getChoiceType()) {
                        case MULTIPLE: {
                            answer.setSelected(b);
                            break;
                        }
                        case SINGLE: {
                            answer.setSelected(b);

                            for (CheckBox c : checkboxes) {
                                c.setChecked(false);
                            }

                            cb.setChecked(true);
                            break;
                        }
                    }
                }
            });

            if (TranslationUtil.contains(answer.getTranslations(), Configuration.getLanguage(context)))
                tvText.setText(TranslationUtil.get(answer.getTranslations(), Configuration.getLanguage(context)));
            else
                tvText.setText(answer.getValue());

            tvText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (c.getChoiceType()) {
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

            addView(llAnswer);
        }
    }
}
