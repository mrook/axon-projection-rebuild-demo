create table tokens (
  processor_name varchar(255) not null,
  segment bigint not null,
  token bytea,
  token_type varchar(255) null,
  timestamp varchar(255) not null,
  owner varchar(255) null,
  primary key (processor_name, segment)
);
