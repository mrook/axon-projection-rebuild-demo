package org.demo;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;

import java.lang.reflect.ParameterizedType;

public class AggregateTest<T> {
	protected FixtureConfiguration<T> fixture;

	@Before
	@SuppressWarnings("unchecked")
	public void createFixture() {
		fixture = new AggregateTestFixture<>((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}
}
