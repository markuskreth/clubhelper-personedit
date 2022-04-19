package de.kreth.clubhelper.personedit.ui.contactedit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.ContactType;
import de.kreth.clubhelper.personedit.ui.components.StoreConfimeListener;
import de.kreth.clubhelper.personedit.ui.components.StoreConfirmedEvent;

public class ContactDialog {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Dialog dlg;
    private final Contact contact;
    private final ComboBox<ContactType> type = new ComboBox<>("Art", ContactType.values());
    private final TextField value = new TextField();

    private Button storeButton;
    private final StoreConfimeListener<Contact> storeListener;
    private final Runnable discardListener;

    public ContactDialog(Contact contact, StoreConfimeListener<Contact> storeListener) {
	this(contact, storeListener, null);
    }

    public ContactDialog(Contact contact, StoreConfimeListener<Contact> storeListener, Runnable discardListener) {
	super();
	this.contact = contact;
	this.storeListener = storeListener;
	this.discardListener = discardListener;
	type.setItemLabelGenerator(ContactType::getName);
	type.setValue(ContactType.valueByName(contact.getType()));
	value.setValue(contact.getValue());
    }

    public void showDialog() {
	if (dlg == null) {
	    dlg = new Dialog();

	    dlg.add(new FormLayout(type, value));
	    storeButton = new Button("Speichern", this::storeChanges);
	    Button discartButton = new Button("Verwerfen", this::discartChanges);

	    dlg.add(new FormLayout(storeButton, discartButton));
	    dlg.open();
	} else {
	    logger.warn("Dialog ist bereits geöffnet. Wird nicht noch einmal geöffnet.");
	}

    }

    void storeChanges(ClickEvent<Button> ev) {
	contact.setType(type.getValue().getName());
	contact.setValue(value.getValue());
	dlg.close();
	dlg = null;
	storeListener.storeConfirmed(new StoreConfirmedEvent<Contact>(contact));
    }

    void discartChanges(ClickEvent<Button> ev) {
	dlg.close();
	dlg = null;
	if (discardListener != null) {
	    discardListener.run();
	}
    }

}
