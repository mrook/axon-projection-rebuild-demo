package org.demo.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfiguration {
	@Bean
	public Gson gson() {
		return new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
				.create();
	}
}
