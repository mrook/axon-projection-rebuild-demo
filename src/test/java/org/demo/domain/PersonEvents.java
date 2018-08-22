package org.demo.domain;

public class PersonEvents {
	public static PersonRegistered personRegistered() {
		return new PersonRegistered(PERSON_ID, NAME, REASON);
	}

	public final static String PERSON_ID = "personId";
	public final static String NAME = "John";
	public final static String REASON = "reason";
}
