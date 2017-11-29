create table if not exists snapshot_events (
	aggregate_id varchar(255) not null,
	sequence_number bigint not null,
	type varchar(255) not null,
	event_id varchar(255) not null,
	metadata bytea,
	payload bytea not null,
	payload_revision varchar(255),
	payload_type varchar(255) not null,
	timestamp varchar(255) not null,
	primary key (aggregate_id, sequence_number),
	unique (event_id)
);
