package de.kreth.clubhelper.personedit.ui.contactedit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.ContactType;
import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.remote.Business;
import de.kreth.clubhelper.personedit.ui.components.StoreConfimeListener;
import de.kreth.clubhelper.personedit.ui.components.StoreConfirmedEvent;
import de.kreth.clubhelper.vaadincomponents.dialog.ConfirmDialog;

public class PersonContact extends Div {

    private static final long serialVersionUID = 8808238171339285576L;
    private Business restService;
    private DetailedPerson personDetails;
    private Grid<Contact> contactGrid;
    private final List<Contact> dataList;
    private ListDataProvider<Contact> dataProvider;
    private Button addContact;

    public PersonContact(Business restService) {
	this.restService = restService;
	this.contactGrid = new Grid<>();
	ItemLabelGenerator<Contact> generator = new ItemLabelGenerator<>() {

	    private static final long serialVersionUID = -7986686553393868535L;

	    @Override
	    public String apply(Contact item) {
		if (item != null && item.getType() != null) {
		    return item.getType().getName();
		}
		return null;
	    }
	};
	contactGrid.addColumn(new TextRenderer<Contact>(generator)).setHeader("Kontakt");
	contactGrid.addColumn(Contact::getValue).setHeader("Wert");

	this.dataList = new ArrayList<>();
	this.dataProvider = DataProvider.ofCollection(dataList);
	contactGrid.setDataProvider(dataProvider);
	contactGrid.setItemDetailsRenderer(createDetailRenderer());
	addContact = new Button(VaadinIcon.PLUS.create(), e -> newContact());
	add(addContact, contactGrid);
    }

    private Renderer<Contact> createDetailRenderer() {
	return new ComponentRenderer<>(
		ContactDetailFormLayout::new, ContactDetailFormLayout::setContact);
    }

    private void startDeleteDialog(Contact c) {
	final Contact contact = Objects.requireNonNull(c);
	ConfirmDialog dlg = new ConfirmDialog();
	dlg.withTitle("Kontakt löschen?");
	dlg.withMessage("Soll der Kontakt \"" + contact + " wirklich gelöscht werden?"
		+ " Diese Löschung kann nicht rückgängig gemacht werden.");
	dlg.withConfirmButton("Löschen", ev -> {
	    personDetails.getContacts().remove(contact);
	    restService.delete(personDetails, contact);
	    dataList.remove(contact);
	    dataProvider.refreshAll();
	});
	dlg.withCancelButton("Abbrechen", null);
	dlg.open();
    }

    private void newContact() {
	Contact contact = new Contact();
	personDetails.getContacts().add(contact);
	dataList.add(contact);
	personDetails.getContacts().add(contact);
	Runnable removeItem = new Runnable() {

	    @Override
	    public void run() {
		personDetails.getContacts().remove(contact);
		dataList.remove(contact);
	    }
	};
	ContactDialog dlg = new ContactDialog(contact, new ContactStoreConfirmListener(), removeItem);
	dlg.showDialog();
    }

    public void init(DetailedPerson personDetails) {
	this.personDetails = personDetails;
	dataList.clear();
	dataList.addAll(personDetails.getContacts());
	dataProvider.refreshAll();
	if (personDetails.getId() < 0) {
	    addContact.setVisible(false);
	    contactGrid.setVisible(false);
	    add(new H2("Kontakte können nur bearbeitet werden, wenn die Person 1x gespeichert wurde."));
	}
    }

    /**
     * Der Listener setzt voraus, dass das übergebene Contact aus dem Event die
     * Änderungen bereits erfahren hat.
     *
     * @author Markus
     *
     */
    class ContactStoreConfirmListener implements StoreConfimeListener<Contact> {

	@Override
	public void storeConfirmed(StoreConfirmedEvent<Contact> ev) {
	    restService.store(personDetails, ev.getStoredItem());
	    dataProvider.refreshAll();
	}

    }

    class ContactDetailFormLayout extends FormLayout {

	private static final long serialVersionUID = -1721499217594558176L;
	private Contact c;

	public void setContact(final Contact c) {
	    this.c = c;
	    ContactType type = c.getType();

	    if (type == ContactType.EMAIL) {
		Button doContact = new Button(VaadinIcon.OUTBOX.create());
		doContact.addClickListener(this::sendMail);
		add(doContact);
	    } else {
		Button doContact = new Button(VaadinIcon.PHONE.create());
		doContact.addClickListener(this::doCall);
		add(doContact);
	    }

	    Button doEdit = new Button(VaadinIcon.EDIT.create());
	    doEdit.addClickListener(this::doEdit);
	    add(doEdit);

	    Button doDelete = new Button(VaadinIcon.TRASH.create());
	    doDelete.addClickListener(this::doDelete);
	    add(doDelete);
	}

	private void doEdit(ClickEvent<Button> ev) {
	    ContactDialog dlg = new ContactDialog(c, new ContactStoreConfirmListener());
	    dlg.showDialog();
	}

	private void doDelete(ClickEvent<Button> ev) {
	    startDeleteDialog(c);
	}

	private void doCall(ClickEvent<Button> ev) {
	    ev.getSource().getUI().ifPresent(ui -> {
		ui.getPage().open("tel:" + c.getValue());
	    });
	}

	private void sendMail(ClickEvent<Button> ev) {
	    ev.getSource().getUI().ifPresent(ui -> {
		ui.getPage().open("mailto:" + c.getValue());
	    });
	}

    }
}
