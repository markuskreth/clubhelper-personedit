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

import de.kreth.clubhelper.personedit.data.Contact;
import de.kreth.clubhelper.personedit.data.DetailedPerson;
import de.kreth.clubhelper.personedit.data.GroupDef;
import de.kreth.clubhelper.personedit.data.Person;
import de.kreth.clubhelper.personedit.data.Startpass;

@Service
public class BusinessImpl implements Business {

    @Autowired
    private RestTemplate webClient;

    @Value("${resourceserver.api.url}")
    private String apiUrl;

    private final Map<Integer, Person> cache = new HashMap<>();

    @Override
    public List<Person> getPersons() {
	String url = apiUrl + "/person";
	Person[] list = webClient.getForObject(url, Person[].class);
	for (Person person : list) {
	    cache.put(person.getId(), person);
	}
	return Arrays.asList(list);
    }

    @Override
    public Authentication getCurrent() {
	return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public DetailedPerson getPersonDetails(Integer personId) {
	Person p;
	if (cache.containsKey(personId)) {
	    p = cache.get(personId);
	} else {
	    String url = apiUrl + "/person/" + personId;
	    p = webClient.getForObject(url, Person.class);
	}

	DetailedPerson detailed = DetailedPerson.createFor(p);

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

    @Cacheable("groups")
    @Override
    public List<GroupDef> getAllGroups() {
	String url = apiUrl + "/group";
	return Arrays.asList(webClient.getForObject(url, GroupDef[].class));
    }

    @Override
    public void store(DetailedPerson bean) {

    }

}
