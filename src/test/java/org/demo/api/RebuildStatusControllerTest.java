package org.demo.api;

import org.axonframework.common.ReflectionUtils;
import org.axonframework.config.Component;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
	TrackingEventProcessor processor;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldGetRebuildStatusFromAProcessor() throws Exception {
		when(processor.getName()).thenReturn("PROCESSOR");

		Configuration config = DefaultConfigurer.defaultConfiguration().buildConfiguration();
		List<Component<Object>> processors = Collections.singletonList(new Component<>(config, "eventHandler", x -> processor));
		Field field = EventHandlingConfiguration.class.getDeclaredField("eventHandlers");
		ReflectionUtils.setFieldValue(field, eventHandlingConfiguration, processors);

		mockMvc
			.perform(get("/rebuild-status"))
			.andExpect(status().isOk())
			.andExpect(content().json("{}"));

		verify(processor).getName();
		verify(processor).processingStatus();
	}
}
