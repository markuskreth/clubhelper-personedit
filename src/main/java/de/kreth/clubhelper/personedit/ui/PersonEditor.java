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
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.data.Gender;
import de.kreth.clubhelper.personedit.data.GroupDef;
import de.kreth.clubhelper.personedit.data.PersonValidator;
import de.kreth.clubhelper.personedit.data.Startpass;
import de.kreth.clubhelper.personedit.remote.Business;

@Route("edit")
@PageTitle("Personeneditor")
public class PersonEditor extends Div
	implements HasUrlParameter<Integer> {

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

    private Button ok;
    private Button reset;

    private Text status;

    private DetailedPerson personDetails;

    public PersonEditor(@Autowired Business restService) {
	this.restService = restService;
	this.binder = new Binder<>();
	this.groups = new ArrayList<>();

	initComponents();
	setupBinder();

    }

    private void setupBinder() {
	PersonValidator validator = new PersonValidator();
	binder.forField(prename).withValidator(validator::validateNameElement)
		.bind(DetailedPerson::getPrename, DetailedPerson::setPrename);
	binder.forField(surname).withValidator(validator::validateNameElement)
		.bind(DetailedPerson::getSurname, DetailedPerson::setSurname);
	binder.forField(birthday).withValidator(validator::validateBirthday)
		.bind(DetailedPerson::getBirth, DetailedPerson::setBirth);
	binder.forField(gender).bind(DetailedPerson::getGenderObject, DetailedPerson::setGender);
	binder.forField(startPass).withValidator(validator::validateStartpass)
		.bind(this::getStartPassNr, this::setStartpassNr);
	binder.forField(groupComponent).withValidator(validator::validateGroup)
		.bind(this::getGroups, this::setGroups);

	binder.setStatusLabel(status);

	binder.addStatusChangeListener(this::binderStatusChange);
    }

    void binderStatusChange(StatusChangeEvent event) {
	if (event.hasValidationErrors()) {
	    ok.setEnabled(false);
	    ok.getElement().setAttribute("title", "Es gibt Fehler, Speichern nicht möglich.");
	} else {
	    ok.setEnabled(true);
	    ok.getElement().setAttribute("title", "Speichern der Eingaben.");
	}
    }

    private void initComponents() {
	FormLayout layoutWithFormItems = new FormLayout();
	prename = new TextField();
	surname = new TextField();
	startPass = new TextField();

	birthday = new DatePicker() {

	    private static final long serialVersionUID = 4447836207485665873L;

	    @Override
	    public LocalDate getEmptyValue() {
		return LocalDate.of(2000, 6, 1);
	    }
	};
	gender = new ComboBox<>();
	gender.setItems(Gender.values());
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

	ok = new Button("OK", this::onOkClick);
	reset = new Button("Zurücksetzen", this::onResetClick);

	layoutWithFormItems.addFormItem(prename, "Vorname");
	layoutWithFormItems.addFormItem(surname, "Nachname");
	layoutWithFormItems.addFormItem(birthday, "Geburtstag");
	layoutWithFormItems.addFormItem(gender, "Geschlecht");
	layoutWithFormItems.addFormItem(groupComponent, "Gruppen");
	layoutWithFormItems.addFormItem(startPass, "Startpassnummer");

	status = new Text("");

	add(layoutWithFormItems, new HorizontalLayout(ok, reset), status);
    }

    public void onResetClick(ClickEvent<Button> event) {
	binder.readBean(personDetails);
    }

    public void onOkClick(ClickEvent<Button> event) {
	if (binder.writeBeanIfValid(personDetails)) {

	    if (binder.hasChanges()) {
		restService.store(binder.getBean());
	    }
	} else {
	    BinderValidationStatus<DetailedPerson> validate = binder.validate();
	    List<BindingValidationStatus<?>> fieldValidationErrors = validate.getFieldValidationErrors();
	    List<ValidationResult> beanValidationErrors = validate.getBeanValidationErrors();
	    System.out.println(fieldValidationErrors);
	    System.out.println(beanValidationErrors);
	}
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
    public void setParameter(BeforeEvent event, Integer personId) {
	if (personId != null) {
	    this.groups.addAll(restService.getAllGroups());
	    groupComponent.getDataProvider().refreshAll();
	    personDetails = restService.getPersonDetails(personId);
	    binder.readBean(personDetails);
	}

    }

}
