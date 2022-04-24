package de.kreth.clubhelper.personedit.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.Lumo;

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
    private NoteKeyChangedListener noteKeyChangedListener = new NoteKeyChangedListener();

    public PersonNotes(Business restService) {
	this.restService = restService;
	Button addKeyButton = new Button("Neuer Schlüssel", this::addKey);

	this.notesKeyBox = new ComboBox<String>() {
	    private static final long serialVersionUID = 5826378017141556665L;

	    @Override
	    public void setItems(Collection<String> items) {
		noteKeyChangedListener.enabled.set(false);
		super.setItems(items);
		noteKeyChangedListener.enabled.set(true);
	    }

	    @Override
	    public void setItems(String... items) {
		noteKeyChangedListener.enabled.set(false);
		super.setItems(items);
		noteKeyChangedListener.enabled.set(true);
	    }
	};
	this.notesKeyBox.setAllowCustomValue(false);
	this.notesKeyBox.addValueChangeListener(noteKeyChangedListener);
	this.notesKeyBox.setItemLabelGenerator(key -> key == null || key.isBlank() ? "Basis" : key);

	storeButton = new Button("Speichern", this::storeChanges);
	storeButton.setEnabled(false);

	this.noteText = new TextArea();
	this.noteText.setLabel("Notzizinhalt");
	this.noteText.setValueChangeMode(ValueChangeMode.LAZY);
	this.noteText.setMaxHeight("50%");
	this.noteText.setWidth("100%");
	this.noteText.setValueChangeTimeout(300);
	this.noteText.addValueChangeListener(this::noteTextChanged);

	VerticalLayout layout = new VerticalLayout();
	layout.add(new FormLayout(addKeyButton, notesKeyBox), noteText);
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

	if (ev.isFromClient() || !Objects.equals(oldValue, value)) {
	    if (noteTextChanged.get() && oldValue != null) {
		noteKeyChangedListener.enabled.set(false);
		ev.getSource().setValue(oldValue);
		noteKeyChangedListener.enabled.set(true);
		Notification.show("Ungespeicherte Änderungen müssen zunächst gespeichert werden.");
		return;
	    }
	    PersonNote personNote = notes.get(value);
	    if (personNote != null) {
		noteText.setValue(personNote.getNotetext());
		noteText.setEnabled(true);
	    } else {
		noteText.setValue("");
		noteText.setEnabled(false);
	    }
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

    private void addKey(ClickEvent<Button> ev) {
	if (noteTextChanged.get()) {
	    Notification.show("Ungespeicherte Änderungen müssen zunächst gespeichert werden.");
	    return;
	}
	Dialog dlg = new Dialog();
	Button okButton = new Button("Notiz erzeugen");
	Button cancelButton = new Button("Abbrechen");
	Label label = new Label("Name für die Notiz");
	TextField text = new TextField();
	text.setPlaceholder("Notizname");
	text.setErrorMessage("Der Text darf nicht leer sein.");
	text.setInvalid(true);
	text.setValueChangeMode(ValueChangeMode.LAZY);
	text.setValueChangeTimeout(300);
	text.addValueChangeListener(ch -> {
	    text.setInvalid(text.getValue().isBlank());
	    okButton.setEnabled(!text.isInvalid());
	});

	dlg.add(label, text, new HorizontalLayout(okButton, cancelButton));

	okButton.addClickListener(click -> {
	    PersonNote note = new PersonNote();
	    note.setNotekey(text.getValue());
	    notes.put(note.getNotekey(), note);
	    notesKeyBox.setItems(notes.keySet());
	    notesKeyBox.setValue(note.getNotekey());
	    dlg.close();
	});

	okButton.addClickShortcut(Key.ENTER);
	okButton.getElement().setAttribute("theme", Lumo.DARK);
	cancelButton.addClickListener(click -> dlg.close());
	dlg.open();
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

    class NoteKeyChangedListener implements ValueChangeListener<ComponentValueChangeEvent<ComboBox<String>, String>> {

	AtomicBoolean enabled = new AtomicBoolean(true);
	private static final long serialVersionUID = 9104381053002323438L;

	@Override
	public void valueChanged(ComponentValueChangeEvent<ComboBox<String>, String> event) {
	    if (enabled.get()) {
		noteKeyChanged(event);
	    }
	}

    }
}
