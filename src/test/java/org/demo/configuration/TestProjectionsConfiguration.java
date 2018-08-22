package org.demo.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.demo.shared.Indices;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import static java.util.Collections.singletonList;

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
				.put("transport.type", "netty4")
				.build();

			return new TestNode(settings, singletonList(Netty4Plugin.class)).start().client();
		} catch (IOException | NodeValidationException exception) {
			throw new RuntimeException(exception);
		}
	}

	private static class TestNode extends Node {
		public TestNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
			super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, null), classpathPlugins);
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
