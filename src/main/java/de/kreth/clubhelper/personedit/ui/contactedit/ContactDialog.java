package de.kreth.clubhelper.personedit.ui.contactedit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.ContactType;
import de.kreth.clubhelper.personedit.ui.components.StoreConfimeListener;
import de.kreth.clubhelper.personedit.ui.components.StoreConfirmedEvent;
import de.kreth.clubhelper.vaadincomponents.validators.ContactValueValidator;

public class ContactDialog {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Dialog dlg;
    private final Contact contact;
    private final ComboBox<ContactType> type = new ComboBox<>("Art", ContactType.values());
    private final TextField value = new TextField();

    private final Button storeButton;
    private final StoreConfimeListener<Contact> storeListener;
    private final Runnable discardListener;
    private Binder<Contact> binder;

    public ContactDialog(Contact contact, StoreConfimeListener<Contact> storeListener) {
	this(contact, storeListener, null);
    }

    public ContactDialog(Contact contact, StoreConfimeListener<Contact> storeListener, Runnable discardListener) {
	super();
	this.contact = contact;
	this.storeListener = storeListener;
	storeButton = new Button("Speichern", this::storeChanges);
	this.discardListener = discardListener;
	type.setItemLabelGenerator(ContactType::getName);
	this.binder = new Binder<Contact>();
	this.binder.forField(value)
		.withValidator(new ContactValueValidator(type)).bind(Contact::getValue,
			Contact::setValue);
	this.binder.forField(type).asRequired("Ein Kontakttyp muss gesetzt sein.").bind(Contact::getType,
		Contact::setType);

	binder.addValueChangeListener(ev -> storeButton.setEnabled(binder.validate().isOk()));
	binder.addStatusChangeListener(event -> storeButton.setEnabled(!event.hasValidationErrors()));
	binder.setBean(contact);

    }

    public void showDialog() {
	if (dlg == null) {
	    dlg = new Dialog();

	    dlg.add(new FormLayout(type, value));
	    Button discartButton = new Button("Verwerfen", this::discartChanges);

	    dlg.add(new FormLayout(storeButton, discartButton));
	    dlg.open();
	} else {
	    logger.warn("Dialog ist bereits geöffnet. Wird nicht noch einmal geöffnet.");
	}

    }

    void storeChanges(ClickEvent<Button> ev) {

	if (binder.validate().isOk()) {
	    dlg.close();
	    dlg = null;
	    storeListener.storeConfirmed(new StoreConfirmedEvent<Contact>(contact));
	}
    }

    void discartChanges(ClickEvent<Button> ev) {
	dlg.close();
	dlg = null;
	if (discardListener != null) {
	    discardListener.run();
	}
    }

}
