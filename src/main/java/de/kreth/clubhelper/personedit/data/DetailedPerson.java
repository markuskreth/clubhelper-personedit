package de.kreth.clubhelper.personedit.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.Startpass;

public class DetailedPerson {

	private long id;
	private LocalDate birth;
	private String prename;
	private String surname;
	private Integer gender;
	private Set<GroupDef> groups;

	private final List<Contact> contacts = new ArrayList<>();

	private Startpass startpass;

	private DetailedPerson() {
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getBirth() {
		return birth;
	}

	public void setBirth(LocalDate birth) {
		this.birth = birth;
	}

	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Gender getGenderObject() {
		if (this.gender != null) {
			return Gender.valueOf(this.gender);
		}
		return null;
	}
	
	public void setGenderObject(Gender gender) {
		if (gender != null) {
			this.setGender(gender.getId());
		} else {
			this.setGender((Integer)null);
		}
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Set<GroupDef> getGroups() {
		return groups;
	}

	public void setGroups(Set<GroupDef> groups) {
		this.groups = groups;
	}

	public Startpass getStartpass() {
		return startpass;
	}

	public void setStartpass(Startpass startpass) {
		this.startpass = startpass;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Collection<Contact> contacts) {
		this.contacts.clear();
		this.contacts.addAll(contacts);
	}

	public static DetailedPerson createFor(Person p) {
		DetailedPerson person = new DetailedPerson();
		person.id = p.getId();
		person.birth = p.getBirth();
		person.prename = p.getPrename();
		person.surname = p.getSurname();
		if (p.getGender() != null) {
			person.gender = p.getGender();
		}
		person.groups = new HashSet<>(p.getGroups());

		return person;
	}

	public Person toPerson(Person person) {
		Person p;
		if (person != null) {
			p = person;
		} else {
			p = new Person();
			p.setId(id);
		}
		p.setBirth(birth);
		p.setPrename(prename);
		p.setSurname(surname);

		if (gender != null) {
			p.setGender(gender);
		}
		p.getGroups().clear();
		p.getGroups().addAll(groups);
		return p;
	}

	@Override
	public String toString() {
		return "DetailedPerson [id=" + id + ", prename=" + prename + ", surname=" + surname + "]";
	}

}
