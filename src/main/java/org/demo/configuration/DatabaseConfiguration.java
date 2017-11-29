package org.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {
	@Bean
	public DataSource dataSource(@Value("${jdbc.host}") String host, @Value("${jdbc.port}") int port, @Value("${jdbc.database}") String database,
								 @Value("${jdbc.username}") String username, @Value("${jdbc.password}") String password) {
		return new DriverManagerDataSource(String.format("jdbc:postgresql://%s:%d/%s", host, port, database), username, password);
	}

	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
