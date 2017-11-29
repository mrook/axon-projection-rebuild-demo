package org.demo.shared;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class Indices {
	private final Client client;

	private final Logger logger;

	@Autowired
	public Indices(Client client) {
		this.client = client;

		logger = LoggerFactory.getLogger(getClass());
	}

	public void refresh(String... indices) {
		client.admin().indices().prepareRefresh(indices).get();
	}

	public void deleteFrom(String... indices) {
		Arrays.stream(client.prepareSearch(indices).get().getHits().getHits()).forEach(hit -> {
			client.prepareDelete(hit.getIndex(), hit.getType(), hit.getId()).get();
		});

		refresh(indices);
	}

}
