package de.kreth.clubhelper.personedit.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.Startpass;
import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.data.Gender;
import de.kreth.clubhelper.personedit.data.PersonValidator;
import de.kreth.clubhelper.personedit.remote.Business;

public class PersonDetails extends Div {

	private static final long serialVersionUID = 1131361447618666217L;
	
	private final List<GroupDef> groups;
	private final Business restService;
	private DetailedPerson personDetails;
	
	private TextField startPass;
	private DatePicker birthday;
	private ComboBox<Gender> gender;

	private CheckboxGroup<GroupDef> groupComponent;

	private Binder<DetailedPerson> binder;

	public PersonDetails(Business restService, Binder<DetailedPerson> binder) {

		this.groups = new ArrayList<>();
		this.binder = binder;
		this.restService = restService;
		this.groups.addAll(restService.getAllGroups());
		
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

		groupComponent = new CheckboxGroup<>();
		groupComponent.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

		groupComponent.setItemLabelGenerator(i -> i.getName() );
		groupComponent.setDataProvider(DataProvider.ofCollection(groups));

		FormLayout layoutWithFormItems = new FormLayout();

		layoutWithFormItems.addFormItem(birthday, "Geburtstag");
		layoutWithFormItems.addFormItem(gender, "Geschlecht");
		layoutWithFormItems.addFormItem(groupComponent, "Gruppen");
		layoutWithFormItems.addFormItem(new HorizontalLayout(startPass, startpassButton), "Startpassnummer");
		
		add(layoutWithFormItems);
		groupComponent.getDataProvider().refreshAll();
	}
	
	public void init(DetailedPerson personDetails) {
		this.personDetails = personDetails;
	}

	public void setupNewPersonGroups(Person newPerson) {
		newPerson.getGroups().add(this.groups.get(0));
	}
	
	private void openStartpassEditor() {
		if (personDetails.getStartpass() != null) {
			personDetails.setStartpass(new Startpass());
		}
		StartpassEditor startpassEditor = new StartpassEditor(personDetails.getStartpass());
		startpassEditor.open();
	}

	public void configBinder(PersonValidator validator) {
		binder.forField(birthday).withValidator(validator::validateBirthday).bind(DetailedPerson::getBirth,
				DetailedPerson::setBirth);

		binder.forField(gender).bind(DetailedPerson::getGenderObject, DetailedPerson::setGenderObject);
		binder.forField(startPass).withValidator(validator::validateStartpass).bind(this::getStartPassNr,
				this::setStartpassNr);
		binder.forField(groupComponent).withValidator(validator::validateGroup)
				.bind(this::getGroups, this::setGroups);

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

}
