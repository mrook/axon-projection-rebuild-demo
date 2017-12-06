package org.demo.api;

import org.axonframework.common.ReflectionUtils;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.EventProcessor;
import org.demo.shared.ProgressTrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Collections;
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
	public Map<String, ?> rebuildStatus() throws NoSuchFieldException {
		Field field = EventHandlingConfiguration.class.getDeclaredField("initializedProcessors");
		List<EventProcessor> initializedProcessors = ReflectionUtils.getFieldValue(field, eventHandlingConfiguration);
		return initializedProcessors.stream()
				.filter(processor -> processor instanceof ProgressTrackingEventProcessor)
				.collect(Collectors.toMap(EventProcessor::getName, processor -> ((ProgressTrackingEventProcessor) processor).getStatus()));
	}
}
