package de.kreth.clubhelper.personedit.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.Accordion.OpenedChangeEvent;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.data.PersonValidator;
import de.kreth.clubhelper.personedit.remote.Business;
import de.kreth.clubhelper.personedit.ui.contactedit.PersonContact;
import de.kreth.clubhelper.vaadincomponents.dialog.ConfirmDialog;

@Route("edit")
@PageTitle("Personeneditor")
public class PersonEditor extends Div implements HasUrlParameter<Long>, BeforeLeaveObserver {

    final Logger logger = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;

    private final Business restService;
    private final Binder<DetailedPerson> binder;
    private TextField prename;
    private TextField surname;

    private PersonDetails details;
    private PersonContact contacts;
    private PersonNotes notes;

    private Button store;

    private Button reset;

    private Button delete;

    private Text status;

    private DetailedPerson personDetails;
    private HorizontalLayout buttonLayout;
    private AccordionPanel datailsAccordionPanel;

    public PersonEditor(@Autowired Business restService) {
	this.restService = restService;
	this.binder = new Binder<>();

	initComponents();
	setupBinder();

    }

    private void initComponents() {
	FormLayout layoutWithFormItems = new FormLayout();
	prename = new TextField();
	surname = new TextField();

	store = new Button("Speichern", VaadinIcon.USER_CHECK.create(), this::onStoreClick);
	store.setEnabled(false); // Keine Änderungen --> disabled.
	reset = new Button("Zurücksetzen", this::onResetClick);
	delete = new Button("Löschen", VaadinIcon.TRASH.create(), this::onDeleteClick);

	details = new PersonDetails(restService, binder);
	contacts = new PersonContact(restService);
	notes = new PersonNotes(restService);

	layoutWithFormItems.addFormItem(prename, "Vorname");
	layoutWithFormItems.addFormItem(surname, "Nachname");

	status = new Text("");

	Accordion accordion = new Accordion();
	datailsAccordionPanel = accordion.add("Personendetails", details);
	accordion.add("Kontakte", contacts);
	accordion.add("Notizen", notes);
	accordion.addOpenedChangeListener(new AccordionChangeListener());
	accordion.open(0);
	buttonLayout = new HorizontalLayout(store, reset, delete);
	add(layoutWithFormItems, accordion, buttonLayout, status);

    }

    private void setupBinder() {
	PersonValidator validator = new PersonValidator();
	binder.forField(prename).withValidator(validator::validateNameElement).asRequired()
		.bind(DetailedPerson::getPrename, DetailedPerson::setPrename);
	binder.forField(surname).withValidator(validator::validateNameElement).asRequired()
		.bind(DetailedPerson::getSurname, DetailedPerson::setSurname);

	details.configBinder(validator);

	binder.addStatusChangeListener(this::binderStatusChange);
    }

    void binderStatusChange(StatusChangeEvent event) {

	if (!binder.isValid()) {
	    store.setEnabled(false);
	    status.setText("Es gibt Fehler, Speichern nicht möglich.");
	} else if (binder.hasChanges()) {
	    store.setEnabled(true);
	    status.setText("Speichern der Eingaben.");
	} else {
	    store.setEnabled(false);
	    status.setText("");
	}
    }

    private void onResetClick(ClickEvent<Button> event) {
	binder.readBean(personDetails);
    }

    private void onStoreClick(ClickEvent<Button> event) {

	if (binder.hasChanges() && binder.writeBeanIfValid(personDetails)) {
	    try {
		DetailedPerson result = restService.store(personDetails);
		if (result.getId() != personDetails.getId()) {
		    binder.removeBean();
		    event.getSource().getUI().ifPresent(ui -> ui.navigate(PersonEditor.class, result.getId()));
		}
		logger.info("Stored {}", personDetails);
		Notification.show("Speichern erfolgreich.");
	    } catch (Exception e) {
		logger.error("Fehler beim Speichern von " + personDetails, e);
		status.setText("Es ist ein Fehler aufgetreten: " + e);
		store.setEnabled(true);
	    }
	}
    }

    private void onDeleteClick(ClickEvent<Button> event) {
	new ConfirmDialog()
		.withTitle("Löschen bestätigen")
		.withMessage("Soll " + personName() + " wirklich gelöscht werden?")
		.withRejectButton("Nein", ev -> {
		})
		.withConfirmButton("Ja", ev -> deletePerson(event))
		.open();
    }

    private void deletePerson(ClickEvent<Button> event) {
	restService.delete(personDetails);
	binder.removeBean();
	event.getSource().getUI().ifPresent(ui -> ui.navigate(MainView.class));
	Notification.show(personDetails + " gelöscht.");
    }

    private String personName() {
	return prename.getValue() + " " + surname.getValue();
    }

    @Override
    public void setParameter(BeforeEvent event, Long personId) {
	if (personId != null) {
	    if (personId < 0) {
		Person newPerson = new Person();
		details.setupNewPersonGroups(newPerson);
		personDetails = DetailedPerson.createFor(newPerson);
		delete.setEnabled(false);
	    } else {
		personDetails = restService.getPersonDetails(personId);
		logger.info("Opening {} for {}", getClass().getSimpleName(), personDetails);
	    }

	    details.init(personDetails);
	    contacts.init(personDetails);
	    notes.init(personDetails);

	    binder.readBean(personDetails);
	    binder.validate();
	}

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

	if (binder.hasChanges() || notes.hasUnsavedChanges()) {
	    logger.debug("Unstored Changes found.");
	    final ContinueNavigationAction postpone = event.postpone();
	    new ConfirmDialog().withTitle("Änderungen werden verworfen!")
		    .withMessage("Es gibt ungespeicherte Änderungen. Wenn Sie fortfahren, werden diese verloren gehen.")
		    .withConfirmButton("Speichern", ev -> {
			if (binder.hasChanges()) {
			    onStoreClick(null);
			    postpone.proceed();
			} else if (notes.hasUnsavedChanges()) {
			    notes.storeChanges();
			}
		    }).withCancelButton("Abbrechen", ev -> {
		    }).withRejectButton("Änderungen verwerfen", ev -> postpone.proceed()).open();
	}

    }

    private class AccordionChangeListener implements ComponentEventListener<Accordion.OpenedChangeEvent> {

	private static final long serialVersionUID = 7027321072276560769L;
	private AccordionPanel lastPanel;
	private boolean isReverting = false;

	@Override
	public void onComponentEvent(OpenedChangeEvent event) {
	    if (!isReverting) {
		if (binder.hasChanges()) {
		    logger.debug("Unstored Changes found.");
		    new ConfirmDialog().withTitle("Änderungen müssen erst gespeichert werden!")
			    .withMessage(
				    "Es gibt ungespeicherte Änderungen. Sie müssen sie speichern, bevor Sie fortfahren.")
			    .withConfirmButton("Speichern", ev -> switchConfirmed(event))
			    .withCancelButton("Abbrechen", ev -> revertSwitch(event))
			    .withRejectButton("Änderungen verwerfen", ev -> discartChanged())
			    .open();
		} else {
		    switchConfirmed(event);
		}

	    }
	}

	private void discartChanged() {
	    binder.readBean(personDetails);
	}

	private void revertSwitch(OpenedChangeEvent event) {
	    isReverting = true;
	    event.getSource().open(lastPanel);
	    isReverting = false;
	}

	private void switchConfirmed(OpenedChangeEvent event) {
	    onStoreClick(null);
	    lastPanel = event.getOpenedPanel().orElse(null);
	    buttonLayout.setVisible(datailsAccordionPanel == lastPanel);
	}
    }
}
