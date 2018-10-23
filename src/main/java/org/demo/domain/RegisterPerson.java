package org.demo.domain;

import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
public class RegisterPerson {
	@TargetAggregateIdentifier
	private final String personId;

	private final String name;

	private final String reason;
}
