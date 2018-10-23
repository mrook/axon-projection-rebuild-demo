package org.demo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.common.IdentifierFactory;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.DataSourceConnectionProvider;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.TokenSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcaster;
import org.demo.upcasters.PersonRegisteredUpcaster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
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
		return EventSchema.builder().eventTable(String.format("events_%d", version)).snapshotTable("snapshot_events")
			.globalIndexColumn("id").aggregateIdentifierColumn("aggregate_id").sequenceNumberColumn("sequence_number").typeColumn("type")
			.eventIdentifierColumn("event_id").metaDataColumn("metadata").payloadColumn("payload").payloadRevisionColumn("payload_revision")
			.payloadTypeColumn("payload_type").timestampColumn("timestamp").build();
	}

	private TokenSchema tokenSchema() {
		return TokenSchema.builder().setTokenTable("tokens").setProcessorNameColumn("processor_name").setSegmentColumn("segment").setTokenColumn("token")
			.setTokenTypeColumn("token_type").setTimestampColumn("timestamp").setOwnerColum("owner").build();
	}

	@Bean
	public TokenStore tokenStore(DataSource dataSource, Serializer serializer) {
		ConnectionProvider connectionProvider = new DataSourceConnectionProvider(dataSource);

		return JdbcTokenStore.builder().connectionProvider(connectionProvider).serializer(serializer)
			.claimTimeout(Duration.ofSeconds(10)).schema(tokenSchema()).contentType(byte[].class)
			.nodeId(ManagementFactory.getRuntimeMXBean().getName()).build();
	}

	private EventUpcaster upcasterChain() {
		return new PersonRegisteredUpcaster();
	}
}
