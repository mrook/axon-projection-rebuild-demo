package org.demo.shared;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventsourcing.eventstore.TrackingToken;
import org.axonframework.messaging.MessageStream;
import org.axonframework.messaging.StreamableMessageSource;

@Slf4j
public class ProgressTrackingMessageSource implements StreamableMessageSource<TrackedEventMessage<?>> {
	private StreamableMessageSource delegate;

	public ProgressTrackingMessageSource(StreamableMessageSource<TrackedEventMessage<?>> delegate) {
		this.delegate = delegate;
	}

	@Override
	public MessageStream openStream(TrackingToken trackingToken) {
		MessageStream stream = delegate.openStream(trackingToken);

		log.info(String.format("opened stream %s", stream));

		return stream;
	}
}
