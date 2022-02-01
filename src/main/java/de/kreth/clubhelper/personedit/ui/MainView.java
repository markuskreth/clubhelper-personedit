package de.kreth.clubhelper.personedit.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
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
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.personedit.remote.Business;

@Route
@PageTitle("Personenliste")
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final List<Person> personList;

	private final Business restService;

	public MainView(@Autowired Business restService) {
		this.restService = restService;
		personList = new ArrayList<>();
		createUi();
		refreshData();
	}

	private void createUi() {

		Button menuButton = new Button(VaadinIcon.MENU.create());
		menuButton.addClickListener(this::onMenuButtonClick);

		Button addButton = new Button(VaadinIcon.PLUS_CIRCLE_O.create());
		addButton.addClickListener(this::onAddButtonClick);

		HorizontalLayout l = new HorizontalLayout(menuButton, new H1("Personen"), addButton);
		l.setAlignItems(Alignment.CENTER);
		add(l);

		TextField filter = new TextField("Filter des Vor- oder Nachnamens");
		filter.setPlaceholder("Filter nach Name...");
		filter.setClearButtonVisible(true);

		Grid<Person> grid = new Grid<>();
		grid.addColumn(Person::getPrename).setHeader("Vorname");
		grid.addColumn(Person::getSurname).setHeader("Nachname");

		ConfigurableFilterDataProvider<Person, Void, SerializablePredicate<Person>> dataProvider = DataProvider
				.ofCollection(this.personList).withConfigurableFilter();
		grid.setDataProvider(dataProvider);
		SerializablePredicate<Person> personFilter = new SerializablePredicate<>() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(Person t) {

				if (filter.getValue() == null || filter.getValue().trim().isEmpty()) {
					return true;
				}

				String filterText = filter.getValue().toLowerCase();
				return t.getSurname().toLowerCase().contains(filterText)
						|| t.getPrename().toLowerCase().contains(filterText);
			}
		};
		dataProvider.setFilter(personFilter);
		filter.addValueChangeListener(p -> dataProvider.refreshAll());

		grid.addItemClickListener(this::personClicked);
		add(grid);

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
		personList.addAll(restService.getPersons());
	}

}
