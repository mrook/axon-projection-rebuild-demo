package org.demo.api;

import org.axonframework.common.ReflectionUtils;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.EventProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.demo.shared.ProgressTrackingEventProcessor;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {RebuildStatusController.class})
public class RebuildStatusControllerTest {
	@MockBean
	EventHandlingConfiguration eventHandlingConfiguration;

	@Mock
	ProgressTrackingEventProcessor processor;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldGetRebuildStatusFromAProcessor() throws Exception {
		when(processor.getName()).thenReturn("PROCESSOR");
		when(processor.getStatus()).thenReturn(processor.new Status(true, 100, 0));

		List<EventProcessor> processors = Collections.singletonList(processor);
		Field field = EventHandlingConfiguration.class.getDeclaredField("initializedProcessors");
		ReflectionUtils.setFieldValue(field, eventHandlingConfiguration, processors);

		mockMvc
				.perform(get("/rebuild-status"))
				.andExpect(status().isOk())
				.andExpect(content().json("{}"));

		verify(processor).getName();
		verify(processor).getStatus();
	}
}
