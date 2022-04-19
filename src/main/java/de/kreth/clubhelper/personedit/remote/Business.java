package de.kreth.clubhelper.personedit.remote;

import java.util.List;

import org.springframework.security.core.Authentication;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.personedit.data.DetailedPerson;

public interface Business {

    List<Person> getPersons();

    Authentication getCurrent();

    List<GroupDef> getAllGroups();

    DetailedPerson getPersonDetails(Long personId);

    DetailedPerson store(DetailedPerson bean);

    void delete(DetailedPerson bean);

    /*
     * Contact Related
     */
    DetailedPerson store(DetailedPerson bean, Contact contact);

    void delete(DetailedPerson personDetails, Contact contact);

}