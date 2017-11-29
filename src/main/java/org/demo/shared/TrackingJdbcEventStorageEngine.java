package org.demo.shared;

import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.eventsourcing.eventstore.TrackedEventData;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcaster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.axonframework.common.jdbc.JdbcUtils.executeQuery;

public class TrackingJdbcEventStorageEngine extends JdbcEventStorageEngine {
	public TrackingJdbcEventStorageEngine(Serializer serializer, EventUpcaster upcasterChain, PersistenceExceptionResolver persistenceExceptionResolver,
										  Integer batchSize, ConnectionProvider connectionProvider, TransactionManager transactionManager, Class<?> dataType,
										  EventSchema schema, Integer maxGapOffset, Long lowestGlobalSequence) {
		super(serializer, upcasterChain, persistenceExceptionResolver, batchSize, connectionProvider, transactionManager, dataType, schema, maxGapOffset,
				lowestGlobalSequence);
	}

	public TrackedEventData<?> getLatestTrackedEvent() throws SQLException {
		return executeQuery(getConnection(),
			this::getLatestTrackedEvent,
			resultSet -> {
				if (resultSet.next()) {
					return getTrackedEventData(resultSet, null);
				}

				return null;
			},
			e -> new EventStoreException("Failed to read events", e)
		);
	}

	private PreparedStatement getLatestTrackedEvent(Connection connection) throws SQLException {
		String sql = "SELECT " + trackedEventFields() + " FROM " + schema().domainEventTable() +
				" ORDER BY " + schema().globalIndexColumn() + " DESC LIMIT 1";

		return connection.prepareStatement(sql);
	}
}
