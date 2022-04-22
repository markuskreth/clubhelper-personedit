package de.kreth.clubhelper.personedit.remote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.kreth.clubhelper.data.BaseEntity;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.PersonNote;
import de.kreth.clubhelper.data.Startpass;
import de.kreth.clubhelper.personedit.data.DetailedPerson;

@Service
public class BusinessImpl implements Business {

    private final RestTemplate webClient;

    @Value("${resourceserver.api.url}")
    private String apiUrl;

    private final Map<Long, Person> cache = new HashMap<>();

    public BusinessImpl(@Autowired RestTemplate webClient) {
	this.webClient = webClient;
    }

    public void setApiUrl(String apiUrl) {
	this.apiUrl = apiUrl;
    }

    @Override
    public List<Person> getPersons() {
	String url = apiUrl + "/person";
	Person[] list = webClient.getForObject(url, Person[].class);
	return Arrays.asList(list);
    }

    @Override
    public Authentication getCurrent() {
	return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public DetailedPerson getPersonDetails(Long personId) {
	DetailedPerson detailed = loadPersonById(personId);

	String url = apiUrl + "/startpass/for/" + personId;
	Startpass[] startpaesse = webClient.getForObject(url, Startpass[].class);
	if (startpaesse != null && startpaesse.length > 0) {
	    detailed.setStartpass(startpaesse[0]);
	}
	url = apiUrl + "/contact/for/" + personId;
	Contact[] contacts = webClient.getForObject(url, Contact[].class);
	detailed.setContacts(Arrays.asList(contacts));
	return detailed;
    }

    private DetailedPerson loadPersonById(Long personId) {
	Person p;
	String url = apiUrl + "/person/" + personId;
	p = webClient.getForObject(url, Person.class);
	cache.put(p.getId(), p);

	return DetailedPerson.createFor(p);
    }

    @Cacheable("groups")
    @Override
    public List<GroupDef> getAllGroups() {
	String url = apiUrl + "/group";
	GroupDef[] forObject = webClient.getForObject(url, GroupDef[].class);
	return Arrays.asList(forObject);
    }

    @Override
    public DetailedPerson store(final DetailedPerson bean) {
	String url;
	DetailedPerson result;
	Person origin = cache.get(bean.getId());
	Person toStore = bean.toPerson(origin);

	url = apiUrl + "/person/" + bean.getId();

	if (bean.getId() < 0) {
	    Person postResult = webClient.postForObject(url, toStore, Person.class);
	    result = DetailedPerson.createFor(postResult);
	} else {
	    webClient.put(url, toStore);
	    result = DetailedPerson.createFor(toStore);
	}
	return result;
    }

    @Override
    public DetailedPerson store(DetailedPerson bean, Contact contact) {

	List<Contact> contacts = bean.getContacts();
	int index = contacts.indexOf(contact);
	Contact c = store(bean, contact, Contact.class);
	contacts.remove(index);
	contacts.add(index, c);
	return bean;
    }

    @Override
    public void delete(final DetailedPerson bean) {
	String url = apiUrl + "/person/" + bean.getId();
	webClient.delete(url);
    }

    @Override
    public void delete(DetailedPerson personDetails, Contact contact) {
	String url = apiUrl + "/contact/" + contact.getId();
	webClient.delete(url);
    }

    @Override
    public List<PersonNote> getPersonNotes(Long personId) {
	String url = apiUrl + "/personnote/for/" + personId;
	PersonNote[] forObject = webClient.getForObject(url, PersonNote[].class);
	return Arrays.asList(forObject);
    }

    @Override
    public <T extends BaseEntity> T store(DetailedPerson bean, T obj, Class<T> storeClass) {

	T result;
	String path = storeClass.getSimpleName().toLowerCase();
	String url = apiUrl + "/" + path
		+ "/for/" + bean.getId();

	if (obj.getId() < 0) {
	    result = webClient.postForObject(url, obj, storeClass);
	} else {
	    webClient.put(url, obj);
	    result = webClient.getForObject(apiUrl + "/" + path
		    + "/" + obj.getId(), storeClass);
	}
	return result;
    }
}
