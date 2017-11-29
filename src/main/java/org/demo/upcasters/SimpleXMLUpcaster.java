package org.demo.upcasters;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.dom4j.Document;

import java.util.stream.Stream;

public abstract class SimpleXMLUpcaster extends XMLUpcaster {
	public SimpleXMLUpcaster(SerializedType consumes, SerializedType typeProduced) {
		super(consumes, typeProduced);
	}

	public SerializedType getTypeProduced() {
		return super.getTypeProduced();
	}

	@Override
	public final Stream<IntermediateEventRepresentation> upcast(Stream<IntermediateEventRepresentation> intermediateRepresentations) {
		return intermediateRepresentations.map(evt -> evt.upcastPayload(getTypeProduced(), Document.class, this::doUpcast));
	}

	protected abstract Document doUpcast(Document document);
}
