package org.demo.projections;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.demo.shared.RebuildableProjection;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.demo.domain.PersonRegistered;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

@Repository
@RebuildableProjection(version = "1", rebuild = true)
@Slf4j
public class PersonProjections extends IndexProjections {
	private final Client client;

	private final Gson gson;

	@Autowired
	public PersonProjections(Client client, Gson gson) throws IOException {
		this.client = client;
		this.gson = gson;
	}

	protected String indexName() {
		return PERSON_INDEX;
	}

	@Override
	protected void createIndex() throws IOException {
		createPersonIndex(client.admin().indices());
	}

	private void createPersonIndex(IndicesAdminClient indicesClient) throws IOException {
		if (!indicesClient.prepareExists(index()).execute().actionGet().isExists()) {
			log.info(String.format("creating index %s for people", index()));

			indicesClient.prepareCreate(index()).addMapping(PERSON_TYPE,
					jsonBuilder().startObject()
							.startObject("properties")
							.startObject("personId")
							.field("type", "keyword")
							.endObject()
							.startObject("name")
							.field("type", "keyword")
							.endObject()
							.endObject()
							.endObject()
			).get();
		}
	}

	@EventHandler
	public void onPersonRegistered(PersonRegistered event) {
		Person person = new Person(event.getPersonId(), event.getName());

		client.prepareIndex(index(), PERSON_TYPE).setId(event.getPersonId()).setSource(gson.toJson(person), JSON).get();
	}

	public Person findByPersonId(String personId) {
		GetResponse response = client.prepareGet(index(), PERSON_TYPE, personId).get();

		return response.isExists() ? gson.fromJson(response.getSourceAsString(), Person.class) : null;
	}

	public Person findByName(String name) {
		SearchHits hits = client.prepareSearch(index()).setTypes(PERSON_TYPE).setQuery(QueryBuilders.termQuery("name", name)).get().getHits();

		return (hits.getTotalHits() == 0L) ? null : gson.fromJson(hits.getHits()[0].getSourceAsString(), Person.class);
	}

	public final static String PERSON_INDEX = "demo.people";
	public final static String PERSON_TYPE = "person";
}
