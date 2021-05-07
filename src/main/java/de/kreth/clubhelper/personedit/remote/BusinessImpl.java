package de.kreth.clubhelper.personedit.remote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;
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
	return Arrays.asList(webClient.getForObject(url, GroupDef[].class));
    }

    @Override
    public DetailedPerson store(final DetailedPerson bean) {

	DetailedPerson result = innerStore(bean);

	List<Contact> contacts = bean.getContacts();
	for (int index = 0; index < contacts.size(); index++) {
	    Contact contact = contacts.get(index);
	    Contact c = storeContact(bean, contact);
	    result.getContacts().add(c);
	}
	return result;
    }

    private DetailedPerson innerStore(final DetailedPerson bean) {
	String url;
	DetailedPerson result;
	Person origin = cache.get(bean.getId());
	if (bean.getId() < 0) {
	    HttpEntity<Person> entity = new HttpEntity<>(bean.toPerson(origin));
	    url = apiUrl + "/person/create";
	    result = DetailedPerson.createFor(webClient.postForObject(url, entity, Person.class));
	} else {
	    url = apiUrl + "/person/" + bean.getId();
	    webClient.put(url, bean);
	    result = DetailedPerson.createFor(bean.toPerson(origin));
	}
	return result;
    }

    private Contact storeContact(final DetailedPerson bean, final Contact contact) {
	String url;
	Contact result;
	if (contact.getId() < 0) {
	    url = apiUrl + "/contact";
	    result = webClient.postForObject(url, contact, Contact.class);
	} else {
	    url = apiUrl + "/contact/for/" + bean.getId();
	    webClient.put(url, contact);
	    result = contact;
	}
	return result;
    }

    @Override
    public void delete(final DetailedPerson bean) {
	String url = apiUrl + "/person/" + bean.getId();
	webClient.delete(url);
    }

}
