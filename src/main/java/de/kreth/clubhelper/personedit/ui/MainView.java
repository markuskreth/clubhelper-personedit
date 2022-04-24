package de.kreth.clubhelper.personedit.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.personedit.remote.Business;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilter;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilterEvent;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilterListener;

@Route
@PageTitle("Personenliste")
public class MainView extends VerticalLayout
	implements ValueChangeListener<ComponentValueChangeEvent<TextField, String>>, GroupFilterListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = 1L;

    private final PersonUiList personList;

    private final Business restService;

    public MainView(@Autowired Business restService) {
	this.restService = restService;
	personList = new PersonUiList();
	createUi();
	refreshData();
    }

    private void createUi() {

	Button menuButton = new Button(VaadinIcon.MENU.create());
	menuButton.addClickListener(this::onMenuButtonClick);

	Button addButton = new Button(VaadinIcon.PLUS_CIRCLE_O.create());
	addButton.addClickListener(this::onAddButtonClick);

	GroupFilter groupFilter = new GroupFilter(restService.getAllGroups());
	groupFilter.addListener(this);

	HorizontalLayout l = new HorizontalLayout(menuButton, new H1("Personen"), addButton);
	l.setAlignItems(Alignment.CENTER);

	TextField filter = new TextField("Filter des Vor- oder Nachnamens");
	filter.setPlaceholder("Filter nach Name...");
	filter.setClearButtonVisible(true);
	filter.addValueChangeListener(this);
	filter.setValueChangeMode(ValueChangeMode.TIMEOUT);
	filter.setValueChangeTimeout(700);

	Grid<Person> grid = new Grid<>();
	grid.addColumn(Person::getPrename).setHeader("Vorname");
	grid.addColumn(Person::getSurname).setHeader("Nachname");

	grid.setDataProvider(personList.getDataProvider());

	filter.addValueChangeListener(this::valueChanged);

	grid.addItemClickListener(this::personClicked);
	setMargin(true);
	add(l);
	add(groupFilter);
	add(filter);
	add(grid);

    }

    @Override
    public void valueChanged(ComponentValueChangeEvent<TextField, String> event) {
	StringBuilder logText = new StringBuilder();
	logText.append("Filtering by Name: " + event.getValue());
	logger.info(logText.toString());
	personList.setFilterText(event.getValue());
    }

    void personClicked(ItemClickEvent<Person> event) {
	Long personId = event.getItem().getId();
	event.getSource().getUI().ifPresent(ui -> ui.navigate(PersonEditor.class, personId));
    }

    public void onAddButtonClick(ClickEvent<Button> event) {
	event.getSource().getUI().ifPresent(ui -> ui.navigate(PersonEditor.class, -1L));
    }

    public void onMenuButtonClick(ClickEvent<Button> event) {
	ContextMenu menu = new ContextMenu();
	menu.setTarget(event.getSource());
	menu.addItem("Einstellungen", this::onSettingsButtonClick);
	menu.addItem("Über", this::onAboutButtonClick);
	menu.setVisible(true);
    }

    public void onSettingsButtonClick(ClickEvent<MenuItem> event) {
	Dialog dlg = new Dialog();
	dlg.add(new H1("Einstellungen"));
	dlg.add(new Text("Einstellugen für diese App. Noch nicht implementiert."));
	dlg.open();
    }

    public void onAboutButtonClick(ClickEvent<MenuItem> event) {

	Dialog dlg = new Dialog();
	dlg.add(new H1("Personeneditor"));
	dlg.add(new Text(
		"Personeneditor ist eine App zur Erfassung und Änderung von Personen im Trampolin des MTV Groß-Buchholz."));
	dlg.open();
    }

    private void refreshData() {
	personList.setPersons(restService.getPersons());
    }

    @Override
    public void groupFilterChange(GroupFilterEvent event) {
	personList.setFilterGroups(event.getFilteredGroups());
    }

}
