package org.apache.zeppelin.graph.neo4jhttp;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Neo4jApiClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jApiClient.class);

	private static final String SERVER_URL = "neo4jhttp.url";
	private static final String AUTH_USERNAME = "neo4jhttp.auth.username";
	private static final String AUTH_PASSWORD = "neo4jhttp.auth.password";
	private static final String AUTH_TYPE = "Authorization";
	private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
	private static final String CONTENT_TEMPLATE = "{\"statements\":[{\"statement\":\"%s\", \"resultDataContents\" : [ \"row\", \"graph\" ]}]}";
	private final OkHttpClient client;
	private final String url;
	private final String username;
	private final String password;

	public Neo4jApiClient(Properties properties) {
		url = properties.getProperty(SERVER_URL);//"http://localhost:7474/db/neo4j/tx/commit";
		username = properties.getProperty(AUTH_USERNAME);//"neo4j";
		password = properties.getProperty(AUTH_PASSWORD);//"hallo123";

		client = new OkHttpClient.Builder().authenticator((route, response) -> {
			final String credential = Credentials.basic(username, password);
			return response.request().newBuilder().header(AUTH_TYPE, credential).build();
		}).build();
	}

	public String executeQuery(String cypherQuery) throws IOException {
		LOGGER.debug("executing query: '" + cypherQuery + "'");

		String requestContent = String.format(CONTENT_TEMPLATE, cypherQuery);
		final RequestBody body = RequestBody.create(requestContent, MEDIA_TYPE);
		final Request request = new Request.Builder().url(url).post(body).build();
		final Response response = client.newCall(request).execute();

		LOGGER.debug("query executed successfully");
		return Objects.requireNonNull(response.body()).string();
	}
}
