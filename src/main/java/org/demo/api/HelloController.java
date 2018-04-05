package org.demo.api;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.IdentifierFactory;
import org.demo.projections.Person;
import org.demo.projections.PersonProjections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.demo.domain.RegisterPerson;

import java.util.Optional;

@RestController
public class HelloController {
	private final CommandGateway commandGateway;
	private final IdentifierFactory identifierFactory;

	private final PersonProjections personProjections;

	private final Logger logger;

	@Autowired
	public HelloController(CommandGateway commandGateway, IdentifierFactory identifierFactory, PersonProjections personProjections) {
		this.commandGateway = commandGateway;
		this.identifierFactory = identifierFactory;

		this.personProjections = personProjections;

		logger = LoggerFactory.getLogger(getClass());
	}

@GetMapping("person/{personId}")
public String getName(@PathVariable("personId") String personId) throws PersonNotFoundException {
	return personProjections.findByPersonId(personId).map(Person::getName)
		.orElseThrow(PersonNotFoundException::new);
}

	@GetMapping("hello")
	public String greet(@RequestParam(value = "name", required = false) String name) {
		name = Optional.ofNullable(name).orElse("world");

		logger.info("greeting {}", name);

		boolean first = (!personProjections.findByName(name).isPresent());

		if (first) {
			commandGateway.sendAndWait(new RegisterPerson(identifierFactory.generateIdentifier(), name, "from the frontend"));
		}

		return String.format("Hello %s%s", first ? "" : "again ", name);
	}
}
