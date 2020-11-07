package de.kreth.clubhelper.personedit.remote;

import java.util.List;

import org.springframework.security.core.Authentication;

import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.data.GroupDef;
import de.kreth.clubhelper.personedit.data.Person;

public interface Business {

    List<Person> getPersons();

    Authentication getCurrent();

    List<GroupDef> getAllGroups();

    DetailedPerson getPersonDetails(Integer personId);

    void store(DetailedPerson bean);

}