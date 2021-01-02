package de.kreth.clubhelper.personedit.data;

import java.time.LocalDate;
import java.util.Collection;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

import de.kreth.clubhelper.data.GroupDef;

/**
 * Validator for fields of {@link DetailedPerson}
 * 
 * @author markus
 *
 */
public class PersonValidator {

    public ValidationResult validateGroup(Collection<GroupDef> groups, ValueContext context) {
	if (groups == null || groups.isEmpty()) {
	    return ValidationResult.error("Mindestens eine Gruppe muss ausgewählt werden.");
	} else {
	    return ValidationResult.ok();
	}
    }

    public ValidationResult validateStartpass(String text, ValueContext context) {
	if (text == null || text.length() == 0 || text.trim().length() > 5) {
	    return ValidationResult.ok();
	} else {
	    return ValidationResult.error("Die Startpassnummer ist zu kurz.");
	}
    }

    public ValidationResult validateNameElement(String text, ValueContext context) {
	if (text != null && text.length() > 0) {
	    return ValidationResult.ok();
	} else {
	    return ValidationResult.error("Bitte einen Namen eingeben.");
	}
    }

    public ValidationResult validateBirthday(LocalDate value, ValueContext context) {
	if (value.isAfter(LocalDate.now().minusYears(150))) {
	    return ValidationResult.ok();
	} else {
	    return ValidationResult.error("Personen können nicht älter als 150 sein.");
	}
    }

}
