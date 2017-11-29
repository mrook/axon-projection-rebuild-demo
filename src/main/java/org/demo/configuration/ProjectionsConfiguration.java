package org.demo.configuration;

import org.axonframework.common.transaction.NoTransactionManager;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.axonframework.eventhandling.LoggingErrorHandler;
import org.axonframework.eventhandling.SimpleEventHandlerInvoker;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.messaging.StreamableMessageSource;
import org.demo.shared.ProgressTrackingEventProcessor;
import org.demo.shared.RebuildableProjection;
import org.demo.shared.TrackingJdbcEventStorageEngine;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Configuration
public class ProjectionsConfiguration {
	@Autowired
	private EventHandlingConfiguration eventHandlingConfiguration;

	@Autowired
	private EventStorageEngine eventStorageEngine;

	@Bean
	public Client client(@Value("${elasticsearch.host}") String host, @Value("${elasticsearch.port}") Integer port) throws UnknownHostException {
		return new PreBuiltTransportClient(Settings.EMPTY)
				.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)));
	}

	@PostConstruct
	public void startTrackingProjections() throws ClassNotFoundException, InterruptedException {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(RebuildableProjection.class));

		for (BeanDefinition bd : scanner.findCandidateComponents("org.demo")) {
			Class<?> aClass = Class.forName(bd.getBeanClassName());
			RebuildableProjection rebuildableProjection = aClass.getAnnotation(RebuildableProjection.class);

			if (rebuildableProjection.rebuild()) {
				ProcessingGroup processingGroup = aClass.getAnnotation(ProcessingGroup.class);

				String name = Optional.ofNullable(processingGroup).map(ProcessingGroup::value)
					.orElse(aClass.getName() + "/" + rebuildableProjection.version());

				eventHandlingConfiguration.assignHandlersMatching(
					name,
					Integer.MAX_VALUE,
					(eventHandler) -> aClass.isAssignableFrom(eventHandler.getClass()));

				registerTrackingProcessor(name);
			}
		}
	}

	private void registerTrackingProcessor(String name) {
		eventHandlingConfiguration.registerEventProcessor(name, (conf, n, handlers) ->
				buildTrackingEventProcessor(conf, name, handlers, org.axonframework.config.Configuration::eventBus));
	}

	private EventProcessor buildTrackingEventProcessor(org.axonframework.config.Configuration conf, String name, List<?> handlers,
													   Function<org.axonframework.config.Configuration,
															   StreamableMessageSource<TrackedEventMessage<?>>> source) {
		return new ProgressTrackingEventProcessor(name, new SimpleEventHandlerInvoker(handlers,
				conf.parameterResolverFactory(),
				conf.getComponent(
						ListenerInvocationErrorHandler.class,
						LoggingErrorHandler::new)),
				(TrackingJdbcEventStorageEngine) eventStorageEngine,
				source.apply(conf),
				conf.getComponent(TokenStore.class, InMemoryTokenStore::new),
				conf.getComponent(TransactionManager.class, NoTransactionManager::instance),
				conf.messageMonitor(EventProcessor.class, name));
	}
}
