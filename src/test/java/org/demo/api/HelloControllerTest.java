package org.demo.api;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.IdentifierFactory;
import org.demo.domain.PersonEvents;
import org.demo.projections.PersonProjections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.demo.domain.RegisterPerson;
import org.demo.projections.Person;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {HelloController.class})
public class HelloControllerTest {
	@MockBean
	private CommandGateway commandGateway;

	@MockBean
	private IdentifierFactory identifierFactory;

	@MockBean
	private PersonProjections personProjections;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldGreetWithName() throws Exception {
		when(personProjections.findByName("operator")).thenReturn(Optional.empty());

		when(identifierFactory.generateIdentifier()).thenReturn(PersonEvents.PERSON_ID);

		mockMvc
				.perform(get("/hello").param("name", "operator"))
				.andExpect(status().isOk())
				.andExpect(content().json("\"Hello operator\""));

		verify(commandGateway).sendAndWait(new RegisterPerson(PersonEvents.PERSON_ID, "operator", "from the frontend"));
	}

	@Test
	public void shouldNotRecreatePerson() throws Exception {
		when(personProjections.findByName("world")).thenReturn(Optional.of(new Person(PersonEvents.PERSON_ID, "world")));

		mockMvc
				.perform(get("/hello"))
				.andExpect(status().isOk())
				.andExpect(content().json("\"Hello again world\""));

		verifyZeroInteractions(commandGateway, identifierFactory);
	}
}
