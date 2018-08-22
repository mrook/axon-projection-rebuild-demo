package org.demo.projections;

import org.demo.domain.PersonEvents;
import org.demo.ProjectionsTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonProjectionsTest extends ProjectionsTest<PersonProjections> {
	protected String getIndex() {
		return projections.index();
	}

	@Test
	public void shouldFindRegisteredPersonByPersonId() {
		publish(PersonEvents.personRegistered());

		assertThat(projections.findByPersonId(PersonEvents.PERSON_ID)).isNotEmpty();
	}

	@Test
	public void shouldNotFindUnregisteredPersonByPersonId() {
		assertThat(projections.findByPersonId(PersonEvents.PERSON_ID)).isEmpty();
	}

	@Test
	public void shouldFindRegisteredPersonByName() {
		publishAndRefresh(PersonEvents.personRegistered());

		assertThat(projections.findByName(PersonEvents.NAME)).isNotEmpty();
	}

	@Test
	public void shouldNotFindUnregisteredPersonByName() {
		assertThat(projections.findByName(PersonEvents.NAME)).isEmpty();
	}
}

