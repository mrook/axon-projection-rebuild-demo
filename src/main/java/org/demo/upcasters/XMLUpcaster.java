package org.demo.upcasters;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.upcasting.event.EventUpcaster;

abstract class XMLUpcaster implements EventUpcaster {
	private final SerializedType typeConsumed;
	private final SerializedType typeProduced;

	XMLUpcaster(SerializedType consumes, SerializedType typeProduced) {
		this.typeConsumed = consumes;
		this.typeProduced = typeProduced;
	}

	public SerializedType getTypeConsumed() {
		return typeConsumed;
	}

	public SerializedType getTypeProduced() {
		return typeProduced;
	}
}
