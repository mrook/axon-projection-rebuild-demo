package org.demo.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.demo.shared.Indices;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;

@Configuration
@ComponentScan(basePackages = "org.demo", includeFilters = @ComponentScan.Filter(Repository.class), useDefaultFilters = false)
public class TestProjectionsConfiguration {
	@Bean
	public Client client() {
		try {
			Settings settings = Settings.builder()
				.put("cluster.routing.allocation.disk.threshold_enabled", false)
				.put("http.enabled", "false")
				.put("node.data", true)
				.put("path.home", Files.createTempDirectory("demo").toAbsolutePath())
				.put("transport.type", "local")
				.build();

			return new Node(settings).start().client();
		} catch (IOException | NodeValidationException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Bean
	public Gson gson() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@Bean
	public Indices indices() {
		return new Indices(client());
	}
}
