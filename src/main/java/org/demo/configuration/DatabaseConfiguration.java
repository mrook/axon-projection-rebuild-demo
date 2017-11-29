package org.demo.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {
	@Bean
	public DataSource dataSource(@Value("${jdbc.host}") String host, @Value("${jdbc.port}") int port, @Value("${jdbc.database}") String database,
								 @Value("${jdbc.username}") String username, @Value("${jdbc.password}") String password) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
