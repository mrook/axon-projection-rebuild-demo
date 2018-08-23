package org.demo.api;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.eventhandling.EventTrackerStatus;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RebuildStatusController {
	private final EventProcessingConfiguration eventProcessingConfiguration;

	@Autowired
	public RebuildStatusController(EventProcessingConfiguration eventProcessingConfiguration) {
		this.eventProcessingConfiguration = eventProcessingConfiguration;
	}

	@GetMapping("rebuild-status")
	public Map<String, Map<Integer, EventTrackerStatus>> rebuildStatus() {
		Map<String, EventProcessor> eventProcessors = eventProcessingConfiguration.eventProcessors();
		return eventProcessors.values().stream()
			.filter(processor -> processor instanceof TrackingEventProcessor)
			.collect(Collectors.toMap(EventProcessor::getName,
				processor -> ((TrackingEventProcessor) processor).processingStatus()
			));
	}
}
