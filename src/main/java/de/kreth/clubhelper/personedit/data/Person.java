package de.kreth.clubhelper.personedit.data;

import java.time.LocalDate;
import java.util.List;

public class Person {

    private int id;
    private LocalDate birth;
    private String prename;
    private String surname;
    private Integer gender;
    private List<GroupDef> groups;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public Gender getGender() {
	if (gender == null) {
	    return null;
	}
	return Gender.valueOf(gender);
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

    public void setGender(Integer gender) {
	this.gender = gender;
    }

    public boolean isMember(GroupDef group) {
	return groups != null && groups.contains(group);
    }

    public List<GroupDef> getGroups() {
	return groups;
    }

    @Override
    public String toString() {
	return "Person [id=" + id + ", prename=" + prename + ", surname=" + surname + "]";
    }

}
