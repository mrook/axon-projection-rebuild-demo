package org.demo.api;

import org.axonframework.common.ReflectionUtils;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.eventhandling.EventTrackerStatus;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RebuildStatusController {
	private final EventHandlingConfiguration eventHandlingConfiguration;

	@Autowired
	public RebuildStatusController(EventHandlingConfiguration eventHandlingConfiguration) {
		this.eventHandlingConfiguration = eventHandlingConfiguration;
	}

	@GetMapping("rebuild-status")
	public Map<String, Map<Integer, EventTrackerStatus>> rebuildStatus() throws NoSuchFieldException {
		Field field = EventHandlingConfiguration.class.getDeclaredField("initializedProcessors");
		List<EventProcessor> initializedProcessors = ReflectionUtils.getFieldValue(field, eventHandlingConfiguration);
		return initializedProcessors.stream()
			.filter(processor -> processor instanceof TrackingEventProcessor)
			.collect(Collectors.toMap(EventProcessor::getName, processor -> ((TrackingEventProcessor) processor).processingStatus()));
	}
}
