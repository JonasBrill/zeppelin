package org.apache.zeppelin.graph.neo4jhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.CypherResult;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Data;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Result;
import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.interpreter.graph.GraphResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.apache.zeppelin.interpreter.InterpreterResult.Code.SUCCESS;

public class Neo4jHttpCypherInterpreter extends Interpreter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jHttpCypherInterpreter.class);
	private final Neo4jApiClient neo4jApiClient;

	public Neo4jHttpCypherInterpreter(Properties properties) {
		super(properties);
		neo4jApiClient = new Neo4jApiClient(properties);
	}

	@Override
	public void open() throws InterpreterException {
		// Neo4jApiClient created during construction! -> no special open mechanism required
	}

	@Override
	public void close() throws InterpreterException {
		// Neo4jApiClient created during construction! -> no special close mechanism required
	}

	@Override
	public void cancel(InterpreterContext interpreterContext) throws InterpreterException {
		//not supported for neo4j
	}

	@Override
	public FormType getFormType() throws InterpreterException {
		return FormType.SIMPLE;
	}

	@Override
	public int getProgress(InterpreterContext interpreterContext) throws InterpreterException {
		//not supported for neo4j
		return 0;
	}

	@Override
	public InterpreterResult interpret(String cypherQuery, InterpreterContext interpreterContext) throws InterpreterException {
		cypherQuery = StringUtils.trim(cypherQuery);
		String interpreterChoice = "%neo4jhttp";
		if (cypherQuery.startsWith(interpreterChoice)) {
			cypherQuery = cypherQuery.substring(interpreterChoice.length());
		}

		if (StringUtils.isBlank(cypherQuery)) {
			LOGGER.debug("Query: '" + cypherQuery + "'\nQuery was empty");
			return new InterpreterResult(SUCCESS);
		}

		try {
			String jsonResult = neo4jApiClient.executeQuery(cypherQuery);
			LOGGER.debug("Query: '" + cypherQuery + "'\nResult: '" + jsonResult + "'");
			return convertJsonToInterpreterResult(jsonResult);
		} catch (JsonProcessingException e) {
			LOGGER.error("Can't parse the JSON result", e);
			throw new InterpreterException("Can't parse the JSON result" + e);
		} catch (IOException e) {
			LOGGER.error("Can't execute cypher query", e);
			throw new InterpreterException("Can't execute cypher query", e);
		}
	}

	public InterpreterResult convertJsonToInterpreterResult(String json) throws JsonProcessingException {
		//map JSON to helper objects
		final ObjectMapper mapper = new ObjectMapper();
		final CypherResult cypherResult = mapper.readValue(json, CypherResult.class);

		if (cypherResult.hasErrors()) {
			LOGGER.warn("Cypher query execution fail. Caused by: " + cypherResult.getFirstErrorMessage());
			return new InterpreterResult(Code.ERROR, cypherResult.getFirstErrorMessage());
		}

		if (cypherResult.isMultiResult()) {
			LOGGER.warn("Multiple statements are not supported");
			return new InterpreterResult(Code.ERROR, "Multiple statements are not supported");
		}

		Result result = cypherResult.results.get(0);

		//collector for graph data
		GraphResultBuilder graphResultBuilder = new GraphResultBuilder();
		TableResultBuilder tableResultBuilder = new TableResultBuilder();

		tableResultBuilder.setColumnsList(result.columns);

		List<Data> data = result.data;

		if (data.isEmpty()) {
			LOGGER.debug("Cypher query returned no result");
			return new InterpreterResult(SUCCESS, tableResultBuilder.build());
		}

		for (Data d : data) {
			tableResultBuilder.addLines(d);
			graphResultBuilder.addGraph(d.graph);
		}

		if (tableResultBuilder.needGraph) {
			LOGGER.debug("Cypher query return graph only result");
			InterpreterResult interpreterResult = new GraphResult(SUCCESS, graphResultBuilder.build());
			interpreterResult.add(tableResultBuilder.build());

			return interpreterResult;
		}
		LOGGER.debug("Cypher query return table only result");
		return new InterpreterResult(SUCCESS, tableResultBuilder.build());

	}
}
