package org.demo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.common.IdentifierFactory;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.DataSourceConnectionProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.TokenSchema;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcaster;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.demo.shared.TrackingJdbcEventStorageEngine;
import org.demo.upcasters.PersonRegisteredUpcaster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.time.Duration;
import javax.sql.DataSource;

@Slf4j
@Configuration
public class EventSourcingConfiguration {
	private static final int EVENT_TABLE_VERSION = 1;

	@Bean
	public IdentifierFactory identifierFactory() {
		return IdentifierFactory.getInstance();
	}

	private EventSchema eventSchema(int version) {
		return EventSchema.builder().withEventTable(String.format("events_%d", version)).withSnapshotTable("snapshot_events")
			.withGlobalIndexColumn("id").withAggregateIdentifierColumn("aggregate_id").withSequenceNumberColumn("sequence_number").withTypeColumn("type")
			.withEventIdentifierColumn("event_id").withMetaDataColumn("metadata").withPayloadColumn("payload").withPayloadRevisionColumn("payload_revision")
			.withPayloadTypeColumn("payload_type").withTimestampColumn("timestamp").build();
	}

	private TokenSchema tokenSchema() {
		return TokenSchema.builder().setTokenTable("tokens").setProcessorNameColumn("processor_name").setSegmentColumn("segment").setTokenColumn("token")
			.setTokenTypeColumn("token_type").setTimestampColumn("timestamp").setOwnerColum("owner").build();
	}

	@Bean
	public TokenStore tokenStore(DataSource dataSource, Serializer serializer) throws SQLException {
		ConnectionProvider connectionProvider = new DataSourceConnectionProvider(dataSource);

		return new JdbcTokenStore(connectionProvider, serializer, tokenSchema(), Duration.ofSeconds(10),
			ManagementFactory.getRuntimeMXBean().getName(), byte[].class);
	}

	private EventUpcaster upcasterChain() {
		return new PersonRegisteredUpcaster();
	}

	@Bean
	public EventStorageEngine eventStorageEngine(DataSource dataSource, PlatformTransactionManager platformTransactionManager) {
		ConnectionProvider connectionProvider = new DataSourceConnectionProvider(dataSource);
		TransactionManager transactionManager = new SpringTransactionManager(platformTransactionManager);

		return new TrackingJdbcEventStorageEngine(null, upcasterChain(), null, null, null, connectionProvider,
			transactionManager, byte[].class, eventSchema(EVENT_TABLE_VERSION), null, null);
	}
}
