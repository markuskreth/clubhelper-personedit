package de.kreth.clubhelper.personedit.ui;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.renderer.TextRenderer;

import de.kreth.clubhelper.data.Gender;

public class GenderRenderer extends TextRenderer<Gender> {

    private static final long serialVersionUID = 126262122798560522L;

    public GenderRenderer() {
	super(new GenderItemLabelGenerator());
    }

    private static String toString(Gender gender) {
	if (gender == Gender.MALE) {
	    return "männlich";
	} else if (gender == Gender.FEMALE) {
	    return "weiblich";
	}
	throw new IllegalStateException("Gender unknown: " + gender);
    }

    private static class GenderItemLabelGenerator implements ItemLabelGenerator<Gender> {

	private static final long serialVersionUID = 3839266535951649071L;

	@Override
	public String apply(Gender item) {
	    return GenderRenderer.toString(item);
	}

    }
}
