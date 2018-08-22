package org.demo.domain;

public class PersonEvents {
	public static PersonRegistered personRegistered() {
		return new PersonRegistered(PERSON_ID, NAME, REASON);
	}

	public static final String PERSON_ID = "personId";
	public static final String NAME = "John";
	public static final String REASON = "reason";
}
