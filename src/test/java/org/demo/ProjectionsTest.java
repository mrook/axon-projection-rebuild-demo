package org.demo;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.SimpleEventHandlerInvoker;
import org.axonframework.eventhandling.SubscribingEventProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.demo.configuration.TestProjectionsConfiguration;
import org.demo.shared.Indices;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestProjectionsConfiguration.class)
public abstract class ProjectionsTest<P> {
	protected P projections;

	@Autowired
	private Indices indices;

	private EventBus eventBus;

	private SubscribingEventProcessor eventProcessor;

	@Before
	public void createEventBus() {
		eventBus = new SimpleEventBus();

		eventProcessor = new SubscribingEventProcessor("listener", new SimpleEventHandlerInvoker(projections), eventBus);

		eventProcessor.start();
	}

	@Before
	public void clearIndex() {
		indices.deleteFrom(getIndex());
	}

	@After
	public void stopEventListener() {
		eventProcessor.shutDown();
	}

	@Autowired
	public void setProjections(P projections) {
		this.projections = projections;
	}

	protected abstract String getIndex();

	protected void publish(Object... events) {
		publishAt(null, events);
	}

	protected void publishAt(Instant instant, Object... events) {
		Clock previousClock = GenericEventMessage.clock;

		try {
			GenericEventMessage.clock = (instant == null) ? previousClock : Clock.fixed(instant, ZoneId.of("UTC"));

			Arrays.stream(events).forEach(event -> eventBus.publish(GenericEventMessage.asEventMessage(event)));
		} finally {
			GenericEventMessage.clock = previousClock;
		}
	}

	protected void publishAndRefresh(Object... events) {
		publishAndRefreshAt(null, events);
	}

	protected void publishAndRefreshAt(Instant instant, Object... events) {
		publishAt(instant, events);

		refreshIndex();
	}

	private void refreshIndex() {
		indices.refresh(getIndex());
	}
}
