package de.kreth.clubhelper.personedit.ui;

import com.vaadin.flow.component.ItemLabelGenerator;

import de.kreth.clubhelper.personedit.data.Gender;

public class GenderItemLabelGenerator implements ItemLabelGenerator<Gender> {

    private static final long serialVersionUID = 126262122798560522L;

    @Override
    public String apply(Gender gender) {
	if (gender == Gender.MALE) {
	    return "männlich";
	} else if (gender == Gender.FEMALE) {
	    return "weiblich";
	}
	throw new IllegalStateException("Gender unknown: " + gender);
    }

}
