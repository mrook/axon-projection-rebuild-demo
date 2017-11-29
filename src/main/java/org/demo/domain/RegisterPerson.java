package org.demo.domain;

import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
public class RegisterPerson {
	@TargetAggregateIdentifier
	private final String personId;

	private final String name;

	private final String reason;
}
