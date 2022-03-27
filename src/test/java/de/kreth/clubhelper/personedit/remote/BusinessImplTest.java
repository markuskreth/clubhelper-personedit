package de.kreth.clubhelper.personedit.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.personedit.data.DetailedPerson;

public class BusinessImplTest {

	@Mock
	RestTemplate restMock;

	private BusinessImpl business;

	private final String apiUrl = "http://localhost";
	private Person person;

	@BeforeEach
	void initBusiness() {

		MockitoAnnotations.initMocks(this);

		business = new BusinessImpl(restMock);
		business.setApiUrl(apiUrl);

		person = new Person();
		person.setId(1);
		person.setGender(1);
		person.setPrename("prename");
		person.setSurname("surname");
		person.setBirth(LocalDate.of(2000, 1, 1));
		GroupDef g1 = new GroupDef();
		g1.setId(1);
		g1.setName("Group 1");
		List<GroupDef> groupList = new ArrayList<GroupDef>();
		groupList.add(g1);
		person.getGroups().addAll(groupList);

	}

	@Test
	void testAddGroup() {

		DetailedPerson detail = DetailedPerson.createFor(person);
		GroupDef g1 = new GroupDef();
		g1.setId(1);
		g1.setName("Group 1");
		detail.getGroups().add(g1);
	}

	@Test
	@Disabled
	void testInsertContact() {

		DetailedPerson detail = DetailedPerson.createFor(person);
		Contact newContact = new Contact();
		newContact.setType(Contact.Type.MOBILE.getName());
		newContact.setValue("0155555555");
		detail.getContacts().add(newContact);
		when(restMock.postForObject(anyString(), any(), eq(Contact.class))).thenAnswer(i -> i.getArgument(1));

		business.store(detail);

		ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);
		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Class<Contact>> contactClassCaptor = ArgumentCaptor.forClass(Class.class);

		verify(restMock).postForObject(uriCaptor.capture(), captor.capture(), contactClassCaptor.capture());
		assertEquals(apiUrl + "/contact", uriCaptor.getValue());
		assertEquals(newContact, captor.getValue());
		assertEquals(Contact.class, contactClassCaptor.getValue());
	}

	@Test
	@Disabled
	void testChangeContact() {

		DetailedPerson detail = DetailedPerson.createFor(person);
		Contact newContact = new Contact();
		newContact.setId(1);
		newContact.setType(Contact.Type.MOBILE.getName());
		newContact.setValue("0155555555");
		detail.getContacts().add(newContact);

		business.store(detail);

		verify(restMock).put(eq(apiUrl + "/contact/for/1"), eq(newContact));

	}

	@Test
	void mockitoCheck() {
		List<String> list = Mockito.mock(List.class);
		String theString = "Test123";
		list.add(theString);
		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		verify(list).add(stringCaptor.capture());
		assertEquals(theString, stringCaptor.getValue());
	}
}
