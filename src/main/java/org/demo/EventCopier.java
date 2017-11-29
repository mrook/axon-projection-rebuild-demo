package org.demo;

import lombok.extern.log4j.Log4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.demo.shared.RebuildableProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@ProcessingGroup("eventCopier")
@Log4j
@RebuildableProjection
@Service
public class EventCopier {
	private final EventStorageEngine eventStorageEngineNextVersion;

	@Autowired
	public EventCopier(@Qualifier("eventStorageEngineNextVersion") EventStorageEngine eventStorageEngineNextVersion) {
		this.eventStorageEngineNextVersion = eventStorageEngineNextVersion;
	}

	@EventHandler
	public void onEvent(EventMessage<?> eventMessage) {
		if (!(eventMessage instanceof DomainEventMessage)) {
			return;
		}

		eventStorageEngineNextVersion.appendEvents((DomainEventMessage) eventMessage);
	}
}
