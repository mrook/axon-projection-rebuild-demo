package org.demo;

import lombok.extern.log4j.Log4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.IdentifierFactory;
import org.demo.domain.RegisterPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.demo.projections.PersonProjections;

@Component
@Log4j
public class InitialData {
	private final CommandGateway commandGateway;
	private final IdentifierFactory identifierFactory;
	private final PersonProjections personProjections;

	@Autowired
	public InitialData(CommandGateway commandGateway, IdentifierFactory identifierFactory, PersonProjections personProjections) {
		this.commandGateway = commandGateway;
		this.identifierFactory = identifierFactory;
		this.personProjections = personProjections;
	}

	@EventListener(ApplicationPreparedEvent.class)
	public void initialize() {
		log.info("Application prepared, adding initial data");
		if (personProjections.findByName("Testperson") == null) {
			commandGateway.sendAndWait(new RegisterPerson(identifierFactory.generateIdentifier(), "Testperson", "Initial test data"));
		}
	}
}
