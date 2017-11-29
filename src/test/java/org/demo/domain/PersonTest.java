package org.demo.domain;

import org.demo.AggregateTest;
import org.demo.domain.Person;
import org.demo.domain.PersonEvents;
import org.demo.domain.PersonRegistered;
import org.demo.domain.RegisterPerson;
import org.junit.Test;

public class PersonTest extends AggregateTest<Person> {
	@Test
	public void shouldBeRegistered() {
		fixture.givenNoPriorActivity()
				.when(new RegisterPerson(PersonEvents.PERSON_ID, PersonEvents.NAME, PersonEvents.REASON))
				.expectEvents(new PersonRegistered(PersonEvents.PERSON_ID, PersonEvents.NAME, PersonEvents.REASON));
	}
}
