package de.kreth.clubhelper.personedit.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializablePredicate;

import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;

public class PersonUiList implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Person> persons;
	private final List<GroupDef> filteredGroups = new ArrayList<>();
	private String filterText;
	private ConfigurableFilterDataProvider<Person, Void, SerializablePredicate<Person>> ofCollection;

	public PersonUiList() {
		persons = new ArrayList<>();
		ofCollection = DataProvider.ofCollection(persons).withConfigurableFilter();
		ofCollection.setFilter(this::matches);
	}

	public void setPersons(Collection<Person> update) {
		persons.clear();
		persons.addAll(update);
		ofCollection.refreshAll();
	}

	public void update(Person result) {
		int index = persons.indexOf(result);
		persons.remove(index);
		persons.add(index, result);

		ofCollection.refreshItem(result);
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText.toLowerCase();
		ofCollection.refreshAll();

	}

	private boolean matches(Person person) {
		boolean filterTextMatches = filterText == null || filterText.trim().isEmpty()
				|| person.getPrename().toLowerCase().contains(filterText)
				|| person.getSurname().toLowerCase().contains(filterText);

		boolean filterGroupMatches = person.getGroups().isEmpty();
		if (!filterGroupMatches) {
			for (GroupDef groupDef : filteredGroups) {
				if (person.isMember(groupDef)) {
					filterGroupMatches = true;
					break;
				}
			}
		}

		return filterTextMatches && filterGroupMatches;
	}

	public DataProvider<Person, ?> getDataProvider() {
		return ofCollection;
	}

	public void sort(Function<Person, Comparable<?>> sortBy) {
		Comparator<Person> comparator = new Comparator<>() {

			@Override
			public int compare(Person o1, Person o2) {
				@SuppressWarnings("unchecked")
				Comparable<Comparable<?>> sortValue1 = (Comparable<Comparable<?>>) sortBy.apply(o1);
				Comparable<?> sortValue2 = sortBy.apply(o2);
				return sortValue1.compareTo(sortValue2);
			}
		};
		persons.sort(comparator);
		ofCollection.refreshAll();
	}
	
	public void setFilterGroups(List<GroupDef> filteredGroups) {
		this.filteredGroups.clear();
		this.filteredGroups.addAll(filteredGroups);
		ofCollection.refreshAll();
	}

}
