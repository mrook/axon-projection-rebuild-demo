package org.demo.upcasters;

import org.axonframework.serialization.SimpleSerializedType;
import org.dom4j.Document;
import org.demo.domain.PersonRegistered;

public class PersonRegisteredUpcaster extends SimpleXMLUpcaster {
	public PersonRegisteredUpcaster() {
		super(
				new SimpleSerializedType(PersonRegistered.class.getTypeName(), null),
				new SimpleSerializedType(PersonRegistered.class.getTypeName(), "1")
		);
	}

	@Override
	protected Document doUpcast(Document document) {
		document.getRootElement().addElement("reason").setText("No reason supplied");
		return document;
	}
}
