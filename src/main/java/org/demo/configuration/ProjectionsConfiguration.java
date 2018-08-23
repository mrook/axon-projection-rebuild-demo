package org.demo.configuration;

import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.demo.shared.RebuildableProjection;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import javax.annotation.PostConstruct;

@Configuration
public class ProjectionsConfiguration {
	@Autowired
	private EventHandlingConfiguration eventHandlingConfiguration;

	@Autowired
	private EventProcessingConfiguration eventProcessingConfiguration;

	@Autowired
	private EventStorageEngine eventStorageEngine;

	@Bean
	public Client client(@Value("${elasticsearch.host}") String host, @Value("${elasticsearch.port}") Integer port) throws UnknownHostException {
		Settings settings = Settings.builder()
			.put("client.transport.ignore_cluster_name", true)
			.build();
		return new PreBuiltTransportClient(settings)
			.addTransportAddress(new TransportAddress(new InetSocketAddress(host, port)));
	}

	@PostConstruct
	public void startTrackingProjections() throws ClassNotFoundException {
		ClassPathScanningCandidateComponentProvider scanner =
			new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(RebuildableProjection.class));

		for (BeanDefinition bd : scanner.findCandidateComponents("org.demo")) {
			Class<?> aClass = Class.forName(bd.getBeanClassName());
			RebuildableProjection rebuildableProjection = aClass.getAnnotation(RebuildableProjection.class);

			if (rebuildableProjection.rebuild()) {
				registerRebuildableProjection(aClass, rebuildableProjection);
			}
		}
	}

	private void registerRebuildableProjection(Class<?> aClass, RebuildableProjection rebuildableProjection) {
		ProcessingGroup processingGroup = aClass.getAnnotation(ProcessingGroup.class);

		String name = Optional.ofNullable(processingGroup).map(ProcessingGroup::value)
			.orElse(aClass.getName() + "/" + rebuildableProjection.version());

		eventHandlingConfiguration.assignHandlersMatching(
			name,
			Integer.MAX_VALUE,
			(eventHandler) -> aClass.isAssignableFrom(eventHandler.getClass()));

		eventProcessingConfiguration.registerTrackingEventProcessor(name);
	}

//	private void registerTrackingProcessor(String name) {
//		eventHandlingConfiguration.registerEventProcessor(name,
//			(conf, n, handlers) -> buildTrackingEventProcessor(conf, name, handlers));
//	}
//
//	private EventProcessor buildTrackingEventProcessor(org.axonframework.config.Configuration conf, String name, List<?> handlers) {
//		return new ProgressTrackingEventProcessor(name, new SimpleEventHandlerInvoker(handlers,
//				conf.parameterResolverFactory(),
//				conf.getComponent(
//						ListenerInvocationErrorHandler.class,
//						LoggingErrorHandler::new)),
//				(TrackingJdbcEventStorageEngine) eventStorageEngine,
//				new ProgressTrackingMessageSource(conf.eventBus()),
//				conf.getComponent(TokenStore.class, InMemoryTokenStore::new),
//				conf.getComponent(TransactionManager.class, NoTransactionManager::instance),
//				conf.messageMonitor(EventProcessor.class, name));
//	}
}
