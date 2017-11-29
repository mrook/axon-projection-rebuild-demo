package org.demo.projections;

import org.demo.shared.RebuildableProjection;

import javax.annotation.PostConstruct;
import java.io.IOException;

public abstract class IndexProjections {
	protected String version() {
		RebuildableProjection annotation = getClass().getAnnotation(RebuildableProjection.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Expected @RebuildableProjection annotation on " + this.getClass());
		} else {
			return annotation.version();
		}
	}

	protected abstract String indexName();

	public final String index() {
		if (version().isEmpty()) {
			return indexName();
		} else {
			return String.format("%s_%s", indexName(), version());
		}
	}

	@PostConstruct
	abstract protected void createIndex() throws IOException;
}
