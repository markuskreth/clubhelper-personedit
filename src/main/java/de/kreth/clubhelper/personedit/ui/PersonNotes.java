package de.kreth.clubhelper.personedit.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.kreth.clubhelper.data.PersonNote;
import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.remote.Business;
import de.kreth.clubhelper.personedit.ui.components.WithUnsavedChangesSupport;

public class PersonNotes extends Div implements WithUnsavedChangesSupport {

    private static final long serialVersionUID = -9204751115060749706L;
    private final Business restService;
    private final ComboBox<String> notesKeyBox;
    private final TextArea noteText;
    private final Button storeButton;
    private AtomicBoolean noteTextChanged = new AtomicBoolean();
    private final Map<String, PersonNote> notes = new HashMap<>();
    private DetailedPerson personDetails;

    public PersonNotes(Business restService) {
	this.restService = restService;
	this.notesKeyBox = new ComboBox<String>();
	this.notesKeyBox.addValueChangeListener(this::noteKeyChanged);
//	this.notesKeyBox.setRenderer(new TextRenderer<String>());
	this.notesKeyBox.setItemLabelGenerator(key -> key == null || key.isBlank() ? "Basis" : key);

	storeButton = new Button("Speichern", this::storeChanges);
	storeButton.setEnabled(false);

	this.noteText = new TextArea();
	this.noteText.setValueChangeMode(ValueChangeMode.LAZY);
	this.noteText.setValueChangeTimeout(1000);
	this.noteText.addValueChangeListener(this::noteTextChanged);
	FormLayout layout = new FormLayout();
	layout.add(notesKeyBox, noteText);
	layout.add(storeButton);
	this.add(layout);
    }

    private void noteTextChanged(ComponentValueChangeEvent<TextArea, String> event) {
	if (event.isFromClient()) {
	    noteTextChanged.set(true);
	}
	storeButton.setEnabled(noteTextChanged.get());
    }

    private void noteKeyChanged(ComponentValueChangeEvent<ComboBox<String>, String> ev) {
	String oldValue = ev.getOldValue();
	String value = ev.getValue();
	if (ev.isFromClient() || !value.equals(oldValue)) {
	    if (noteTextChanged.get()) {
		ev.getSource().setValue(ev.getOldValue());
		Notification.show("Ungespeicherte Änderungen müssen zunächst gespeichert werden.");
		return;
	    }
	    noteText.setValue(notes.get(ev.getValue()).getNotetext());
	}

	storeButton.setEnabled(noteTextChanged.get());
    }

    public void init(DetailedPerson personDetails) {
	notes.clear();
	this.personDetails = personDetails;
	List<PersonNote> noteList = restService.getPersonNotes(personDetails.getId());
	noteList.forEach(n -> notes.put(n.getNotekey(), n));
	if (notes.get(null) != null) {
	    PersonNote nullValue = notes.remove(null);
	    int index = noteList.indexOf(nullValue);
	    nullValue.setNotekey("");
	    notes.put("", nullValue);
	    noteList.remove(index);
	    noteList.add(index, nullValue);
	}
	notesKeyBox.setItems(notes.keySet());
	if (!noteList.isEmpty()) {
	    notesKeyBox.setValue(noteList.get(0).getNotekey());
	}

    }

    @Override
    public boolean hasUnsavedChanges() {
	return noteTextChanged.get();
    }

    private void storeChanges(ClickEvent<Button> ev) {
	storeChanges();
    }

    public void storeChanges() {
	if (noteTextChanged.get()) {
	    String selectedKey = notesKeyBox.getValue();
	    PersonNote note = notes.get(selectedKey);
	    note.setNotetext(noteText.getValue());
	    PersonNote stored = restService.store(personDetails, note, PersonNote.class);
	    notes.put(selectedKey, stored);
	    noteTextChanged.set(false);
	    storeButton.setEnabled(noteTextChanged.get());
	}
    }

}
