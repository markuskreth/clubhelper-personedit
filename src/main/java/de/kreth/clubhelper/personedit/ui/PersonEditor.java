package de.kreth.clubhelper.personedit.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.kreth.clubhelper.data.Gender;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.Startpass;
import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.data.PersonValidator;
import de.kreth.clubhelper.personedit.remote.Business;

@Route("edit")
@PageTitle("Personeneditor")
public class PersonEditor extends Div implements HasUrlParameter<Long>, BeforeLeaveObserver {

	private static final long serialVersionUID = 1L;

	private final Business restService;
	private final List<GroupDef> groups;
	private final Binder<DetailedPerson> binder;
	private TextField prename;
	private TextField surname;
	private TextField startPass;
	private DatePicker birthday;
	private ComboBox<Gender> gender;

	private MultiSelectListBox<GroupDef> groupComponent;

	private Button store;

	private Button reset;

	private Button delete;

	private Text status;

	private DetailedPerson personDetails;

	private Text groupError;

	public PersonEditor(@Autowired Business restService) {
		this.restService = restService;
		this.binder = new Binder<>();
		this.groups = new ArrayList<>();

		initComponents();
		setupBinder();

	}

	private void initComponents() {
		FormLayout layoutWithFormItems = new FormLayout();
		prename = new TextField();
		surname = new TextField();
		startPass = new TextField();
		startPass.setAutocapitalize(Autocapitalize.CHARACTERS);
		startPass.setValueChangeMode(ValueChangeMode.EAGER);
		startPass.setEnabled(false);
		Button startpassButton = new Button(VaadinIcon.PENCIL.create(), ev -> openStartpassEditor());

		birthday = new DatePicker() {
			private static final long serialVersionUID = 4447836207485665873L;

			@Override
			public LocalDate getEmptyValue() {
				return LocalDate.of(2000, 6, 1);
			}
		};
		gender = new ComboBox<>();
		gender.setItems(Gender.values());
		gender.setRenderer(new TextRenderer<>(new GenderItemLabelGenerator()));
		gender.setItemLabelGenerator(new GenderItemLabelGenerator());
		gender.setAllowCustomValue(false);

		groupComponent = new MultiSelectListBox<>();
		ComponentRenderer<? extends Component, GroupDef> itemRenderer = new ComponentRenderer<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component createComponent(GroupDef item) {
				return new Text(item.getName());
			}

		};
		groupComponent.setRenderer(itemRenderer);
		groupComponent.setDataProvider(DataProvider.ofCollection(groups));
		groupError = new Text("");
		groupComponent.add(groupError);
		groupError.setText("Hier werden Fehler in Gruppen angezeigt.");

		store = new Button("Speichern", VaadinIcon.USER_CHECK.create(), this::onStoreClick);
		store.setEnabled(false); // Keine Änderungen --> disabled.
		reset = new Button("Zurücksetzen", this::onResetClick);
		delete = new Button("Löschen", VaadinIcon.TRASH.create(), this::onDeleteClick);

		layoutWithFormItems.addFormItem(prename, "Vorname");
		layoutWithFormItems.addFormItem(surname, "Nachname");
		layoutWithFormItems.addFormItem(birthday, "Geburtstag");
		layoutWithFormItems.addFormItem(gender, "Geschlecht");
		layoutWithFormItems.addFormItem(groupComponent, "Gruppen");
		layoutWithFormItems.addFormItem(new HorizontalLayout(startPass, startpassButton), "Startpassnummer");

		status = new Text("");

		add(layoutWithFormItems, new HorizontalLayout(store, reset, delete), status);
	}

	private void setupBinder() {
		PersonValidator validator = new PersonValidator();
		binder.forField(prename).withValidator(validator::validateNameElement).asRequired()
				.bind(DetailedPerson::getPrename, DetailedPerson::setPrename);
		binder.forField(surname).withValidator(validator::validateNameElement).asRequired()
				.bind(DetailedPerson::getSurname, DetailedPerson::setSurname);
		binder.forField(birthday).withValidator(validator::validateBirthday).bind(DetailedPerson::getBirth,
				DetailedPerson::setBirth);

		binder.forField(gender).bind(DetailedPerson::getGenderObject, DetailedPerson::setGender);
		binder.forField(startPass).withValidator(validator::validateStartpass).bind(this::getStartPassNr,
				this::setStartpassNr);
		binder.forField(groupComponent).withValidator(validator::validateGroup)
//		.withStatusLabel(groupError)
				.bind(this::getGroups, this::setGroups);

		binder.setStatusLabel(status);

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

	private void openStartpassEditor() {
		if (personDetails.getStartpass() != null) {
			personDetails.setStartpass(new Startpass());
		}
		StartpassEditor startpassEditor = new StartpassEditor(personDetails.getStartpass());
		startpassEditor.open();
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
			} catch (Exception e) {
				status.setText("Es ist ein Fehler aufgetreten: " + e);
				store.setEnabled(true);
			}
		}
	}

	private void onDeleteClick(ClickEvent<Button> event) {
		new ConfirmDialog()
				.withTitle("Löschen bestätigen")
				.withMessage("Soll " + personName() + " wirklich gelöscht werden?")
				.withRejectButton("Nein", ev -> { })
				.withConfirmButton("Ja", ev -> deletePerson(event))
				.open();
	}

	private void deletePerson(ClickEvent<Button> event) {
		restService.delete(personDetails);
		binder.removeBean();
		event.getSource().getUI().ifPresent(ui -> ui.navigate(MainView.class));
	}

	private String personName() {
		return prename.getValue() + " " + surname.getValue();
	}

	private Set<GroupDef> getGroups(DetailedPerson person) {
		return person.getGroups();
	}

	private void setGroups(DetailedPerson person, Set<GroupDef> groups) {
		if (person.getGroups() == null) {
			person.setGroups(groups);
		} else {
			person.getGroups().clear();
			person.getGroups().addAll(groups);
		}
	}

	private void setStartpassNr(DetailedPerson person, String numer) {
		Startpass startpass = person.getStartpass();
		if (startpass == null) {
			startpass = new Startpass();
			person.setStartpass(startpass);
		}
		startpass.setStartpassNr(numer);
	}

	private String getStartPassNr(DetailedPerson p) {
		Startpass startpass = p.getStartpass();
		if (startpass == null) {
			return "";
		} else {
			return startpass.getStartpassNr();
		}
	}

	@Override
	public void setParameter(BeforeEvent event, Long personId) {
		if (personId != null) {
			this.groups.clear();
			this.groups.addAll(restService.getAllGroups());
			groupComponent.getDataProvider().refreshAll();
			if (personId < 0) {
				Person newPerson = new Person();
				newPerson.getGroups().add(this.groups.get(0));
				personDetails = DetailedPerson.createFor(newPerson);
				delete.setEnabled(false);
			} else {
				personDetails = restService.getPersonDetails(personId);
			}
			binder.readBean(personDetails);
			binder.validate();
		}

	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {

		if (binder.hasChanges()) {
			final ContinueNavigationAction postpone = event.postpone();
			new ConfirmDialog().withTitle("Änderungen werden verworfen!")
					.withMessage("Es gibt ungespeicherte Änderungen. Wenn Sie fortfahren, werden diese verloren gehen.")
					.withConfirmButton("Speichern", ev -> {
						onStoreClick(null);
						postpone.proceed();
					}).withCancelButton("Abbrechen", ev -> {
					}).withRejectButton("Änderungen verwerfen", ev -> postpone.proceed()).open();
		}

	}

}
