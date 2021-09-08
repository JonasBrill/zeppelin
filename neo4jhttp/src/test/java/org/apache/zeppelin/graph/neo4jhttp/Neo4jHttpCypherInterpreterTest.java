package org.apache.zeppelin.graph.neo4jhttp;

import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.graph.GraphResult;
import org.junit.jupiter.api.*;

import java.util.Properties;

import static org.apache.zeppelin.interpreter.InterpreterResult.Code.ERROR;
import static org.apache.zeppelin.interpreter.InterpreterResult.Code.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

public class Neo4jHttpCypherInterpreterTest {

	private Interpreter setupInterpreter() {
		Properties properties = new Properties();
		properties.setProperty("neo4jhttp.url", "http://localhost:7474/db/neo4j/tx/commit");
		properties.setProperty("neo4jhttp.auth.username", "neo4j");
		properties.setProperty("neo4jhttp.auth.password", "hallo123");
		return new Neo4jHttpCypherInterpreter(properties);
	}

	@Nested
	class ErrorTest {
		String query = "Match(n)";
		InterpreterResult res;

		@BeforeEach
		void before() throws InterpreterException {
			res = setupInterpreter().interpret(query, null);
		}

		@Test
		@DisplayName ("type should be InterpreterResult")
		void first() {
			assertTrue(res instanceof InterpreterResult);
		}

		@Test
		@DisplayName ("status code should be ERROR")
		void second() {
			assertEquals(ERROR, res.code());
		}

		@Test
		@DisplayName ("correct result")
		void third() {
			String expected = "Query cannot conclude with MATCH (must be RETURN or an update clause) (line 1, column 1 (offset: 0))\n" + "\"Match(n)\"\n" + " ^";
			assertEquals(expected, res.message().get(0).getData());
		}
	}

	@Nested
	class EmptyQueryTest {
		String query = "";
		InterpreterResult res;

		@BeforeEach
		void before() throws InterpreterException {
			res = setupInterpreter().interpret(query, null);
		}

		@Test
		@DisplayName ("type should be InterpreterResult")
		void first() {
			assertTrue(res instanceof InterpreterResult);
		}

		@Test
		@DisplayName ("status code should be SUCCESS")
		void second() {
			assertEquals(SUCCESS, res.code());
		}

		@Test
		@DisplayName ("no message should be returned")
		void third() {
			assertThrows(IndexOutOfBoundsException.class, ()-> res.message().get(0).getData());
		}
	}

	@Nested
	class QueryWithNumberResult {
		String query = "Match(n) Return count(n)";
		InterpreterResult res;

		@BeforeEach
		void before() throws InterpreterException {
			res = setupInterpreter().interpret(query, null);
		}

		@Test
		@DisplayName ("type should be InterpreterResult")
		void first() {
			assertTrue(res instanceof InterpreterResult);
		}

		@Test
		@DisplayName ("status code should be SUCCESS")
		void second() {
			assertEquals(SUCCESS, res.code());
		}

		@Test
		@DisplayName ("correct result")
		void third() {
			String expected = "count(n)\n" + "7\n";
			assertEquals(expected, res.message().get(0).getData());
		}
	}

	@Nested
	class QueryWithRelationsAsResult {
		String query = "Match(n)-[m]->(l) Return m";
		InterpreterResult res;

		@BeforeEach
		void before() throws InterpreterException {
			res = setupInterpreter().interpret(query, null);
		}

		@Test
		@DisplayName ("type should be InterpreterResult")
		void first() {
			assertTrue(res instanceof InterpreterResult);
		}

		@Test
		@DisplayName ("status code should be SUCCESS")
		void second() {
			assertEquals(SUCCESS, res.code());
		}

		@Test
		@DisplayName ("correct result")
		void third() {
			String expected = "m\n" + "{\"id\":0,\"type\":\"KNOWS\",\"startNode\":0,\"endNode\":1,\"properties\":{}}\n" + "{\"id\":1,\"type\":\"KNOWS\",\"startNode\":2,\"endNode\":3,\"properties\":{}}\n" + "{\"id\":2,\"type\":\"KNOWS\",\"startNode\":4,\"endNode\":5,\"properties\":{}}\n" + "{\"id\":3,\"type\":\"OWNS\",\"startNode\":5,\"endNode\":6,\"properties\":{}}\n";
			assertEquals(expected, res.message().get(0).getData());
		}
	}

	@Nested
	class QueryWithRelationsAndNodesAsResult {
		String query = "Match(n)-[m]->(l) Return n,m";
		InterpreterResult res;

		@BeforeEach
		void before() throws InterpreterException {
			res = setupInterpreter().interpret(query, null);
		}

		@Test
		@DisplayName ("type should be GraphResult")
		void first() {
			assertTrue(res instanceof GraphResult);
		}

		@Test
		@DisplayName ("status code should be SUCCESS")
		void second() {
			assertEquals(SUCCESS, res.code());
		}

		@Test
		@DisplayName ("correct result")
		void third() {
			String expected = "n\tm\n" + "{\"id\":0,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user1\",\"alter\":20}}\t{\"id\":0,\"type\":\"KNOWS\",\"startNode\":0,\"endNode\":1,\"properties\":{}}\n" + "{\"id\":2,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user4\",\"alter\":20}}\t{\"id\":1,\"type\":\"KNOWS\",\"startNode\":2,\"endNode\":3,\"properties\":{}}\n" + "{\"id\":4,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user4\",\"alter\":20}}\t{\"id\":2,\"type\":\"KNOWS\",\"startNode\":4,\"endNode\":5,\"properties\":{}}\n" + "{\"id\":5,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user3\",\"alter\":37}}\t{\"id\":3,\"type\":\"OWNS\",\"startNode\":5,\"endNode\":6,\"properties\":{}}\n";
			assertEquals(expected, res.message().get(1).getData());
			assertNotNull(res.message().get(0).getData());
		}
	}

	@Nested
	class QueryWithPathAsResult {
		String query = "Match p = (n)-[*2]->(l) Return p";
		InterpreterResult res;

		@BeforeEach
		void before() throws InterpreterException {
			res = setupInterpreter().interpret(query, null);
		}

		@Test
		@DisplayName ("type should be GraphResult")
		void first() {
			assertTrue(res instanceof GraphResult);
		}

		@Test
		@DisplayName ("status code should be SUCCESS")
		void second() {
			assertEquals(SUCCESS, res.code());
		}

		@Test
		@DisplayName ("correct result")
		void third() {
			String expected = "p\n" + "{\"start\":{\"id\":4,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user4\",\"alter\":20}},\"end\":{\"id\":6,\"labels\":[\"Tier\"],\"properties\":{\"name\":\"Bello\"}},\"segments\":[{\"start\":{\"id\":4,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user4\",\"alter\":20}},\"relationship\":{\"id\":2,\"type\":\"KNOWS\",\"startNode\":4,\"endNode\":5,\"properties\":{}},\"end\":{\"id\":5,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user3\",\"alter\":37}}},{\"start\":{\"id\":5,\"labels\":[\"Person\"],\"properties\":{\"name\":\"user3\",\"alter\":37}},\"relationship\":{\"id\":3,\"type\":\"OWNS\",\"startNode\":5,\"endNode\":6,\"properties\":{}},\"end\":{\"id\":6,\"labels\":[\"Tier\"],\"properties\":{\"name\":\"Bello\"}}}],\"length\":2}\n";
			assertEquals(expected, res.message().get(1).getData());
			assertNotNull(res.message().get(0).getData());
		}
	}
}
