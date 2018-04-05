package org.demo.upcasters;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.dom4j.Document;

import java.util.stream.Stream;

public abstract class SimpleXMLUpcaster extends XMLUpcaster {
	public SimpleXMLUpcaster(SerializedType typeConsumed, SerializedType typeProduced) {
		super(typeConsumed, typeProduced);
	}

	@Override
	public final Stream<IntermediateEventRepresentation> upcast(
		Stream<IntermediateEventRepresentation> intermediateRepresentations) {
		return intermediateRepresentations.map(evt -> {
			if (evt.getType().equals(getTypeConsumed())) {
				return evt.upcastPayload(getTypeProduced(), Document.class, this::doUpcast);
			} else {
				return evt;
			}
		});
	}

	protected abstract Document doUpcast(Document document);
}
