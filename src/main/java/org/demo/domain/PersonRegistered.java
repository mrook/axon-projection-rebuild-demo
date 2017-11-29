package org.demo.domain;

import lombok.Data;
import org.axonframework.serialization.Revision;

@Data
@Revision("1")
public class PersonRegistered {
	private final String personId;

	private final String name;

	private final String reason;
}
