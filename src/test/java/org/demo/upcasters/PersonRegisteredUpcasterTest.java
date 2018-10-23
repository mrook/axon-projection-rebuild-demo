package org.demo.upcasters;

import org.axonframework.eventhandling.EventData;
import org.axonframework.eventhandling.GenericDomainEventEntry;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackedDomainEventData;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedObject;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.event.InitialEventRepresentation;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.demo.domain.PersonEvents;
import org.demo.domain.PersonRegistered;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PersonRegisteredUpcasterTest {
	@Test
	public void shouldUpcastOldPersonRegisteredEvent() {
		Serializer serializer = XStreamSerializer.builder().build();
		Map<String, ?> metaData = Collections.emptyMap();

		String serializedData = String.format("<org.demo.domain.PersonRegistered><personId>%s</personId><name>%s</name></org.demo.domain.PersonRegistered>",
			PersonEvents.PERSON_ID, PersonEvents.NAME);
		SerializedObject<String> serializedPayload = new SimpleSerializedObject<>(serializedData, String.class,
			new SimpleSerializedType(PersonRegistered.class.getTypeName(), null));

		EventData<?> eventData = new TrackedDomainEventData<Object>(
			new GlobalSequenceTrackingToken(10),
			new GenericDomainEventEntry<>("test", "aggregateId", 0, "eventId", Instant.now(),
				serializedPayload.getType().getName(), serializedPayload.getType().getRevision(), serializedData,
				serializer.serialize(MetaData.emptyInstance(), String.class)));

		PersonRegisteredUpcaster upcaster = new PersonRegisteredUpcaster();

		List<IntermediateEventRepresentation> result = upcaster.upcast(Stream.of(new InitialEventRepresentation(eventData, serializer))).collect(toList());

		assertFalse(result.isEmpty());

		IntermediateEventRepresentation firstEvent = result.get(0);
		assertEquals("1", firstEvent.getType().getRevision());
		PersonRegistered upcastedEvent = serializer.deserialize(firstEvent.getData());
		assertEquals("No reason supplied", upcastedEvent.getReason());
		assertEquals(eventData.getEventIdentifier(), firstEvent.getMessageIdentifier());
		assertEquals(eventData.getTimestamp(), firstEvent.getTimestamp());
	}
}
