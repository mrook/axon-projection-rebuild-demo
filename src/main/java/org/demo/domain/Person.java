package org.demo.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Person {
	@AggregateIdentifier
	private String personId;

	private String name;

	private Person() {}

	@CommandHandler
	public Person(RegisterPerson command) {
		apply(new PersonRegistered(command.getPersonId(), command.getName(), command.getReason()));
	}

	@EventHandler
	public void onPersonRegistered(PersonRegistered event) {
		this.personId = event.getPersonId();
		this.name = event.getName();
	}
}
