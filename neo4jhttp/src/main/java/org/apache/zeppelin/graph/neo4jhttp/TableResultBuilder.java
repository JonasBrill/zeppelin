package org.apache.zeppelin.graph.neo4jhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Data;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Node;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Relationship;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.path.Path;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.path.Segment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TableResultBuilder {
	private static final String NODE = "node";
	private static final String RELATIONSHIP = "relationship";

	private static final String NEW_LINE = "\n";
	private static final String TAB = "\t";
	private static final String TABLE = "%table";

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final List<String> columnsList = new ArrayList<>();
	private final List<List<String>> linesList = new ArrayList<>();

	public boolean needGraph = false;

	public void setColumnsList(List<String> columnsList) {
		this.columnsList.clear();
		this.columnsList.addAll(columnsList);
	}

	/**
	 * Build lines based on metadata type
	 */
	public void addLines(Data data) throws JsonProcessingException {
		int i = 0;
		List<String> innerList = new ArrayList<>();
		for (Object m : data.meta) {
			Object rowObject = data.row.get(i);
			if (m == null) {
				innerList.add(rowObject.toString());
			} else if (m instanceof Map) {
				final Map<String, Object> meta = (Map<String, Object>) m;

				long id = parsLongFromObject(meta, "id");
				if (RELATIONSHIP.equals(meta.get("type"))) {
					addRelationshipToLine(data, innerList, id, rowObject);
				} else if (NODE.equals(meta.get("type"))) {
					needGraph = true;
					addNodeToLine(data, innerList, id, rowObject);
				} else {
					//error
				}
			} else if (m instanceof List) {
				needGraph = true;

				final List<Map<String, Object>> metaList = (List<Map<String, Object>>) m;

				addPathToLine(data, innerList, metaList);
			} else {
				//error
			}
			i++;
		}
		linesList.add(innerList);
	}

	private long parsLongFromObject(Map<String, Object> map, String key) {
		return Long.parseLong(map.get(key).toString());
	}

	private void addRelationshipToLine(Data data, List<String> innerList, long id, Object rowObject) throws JsonProcessingException {
		final Relationship result = getRelationshipFromGraphEntries(data, id);

		if (result != null) {
			innerList.add(objectMapper.writeValueAsString(result));
		} else {
			innerList.add(objectMapper.writeValueAsString(rowObject));
		}
	}

	private void addNodeToLine(Data data, List<String> innerList, long id, Object rowObject) throws JsonProcessingException {
		final Node result = getNodeFromGraphEntries(data, id);

		if (result != null) {
			innerList.add(objectMapper.writeValueAsString(result));
		} else {
			innerList.add(objectMapper.writeValueAsString(rowObject));
		}
	}

	private void addPathToLine(Data data, List<String> innerList, List<Map<String, Object>> metaList) throws JsonProcessingException {
		final Path path = new Path();

		final Map<String, Object> first = metaList.get(0);
		path.start = getNodeFromGraphEntries(data, parsLongFromObject(first, "id"));

		final Map<String, Object> last = metaList.get(metaList.size() - 1);
		path.end = getNodeFromGraphEntries(data, parsLongFromObject(last, "id"));


		Segment segment = new Segment();
		segment.start = path.start;
		for (int i = 1; i < metaList.size(); i += 2) {
			segment.relationship = getRelationshipFromGraphEntries(data, parsLongFromObject(metaList.get(i), "id"));
			Node node = getNodeFromGraphEntries(data, parsLongFromObject(metaList.get(i+1), "id"));
			segment.end = node;

			path.segments.add(segment);
			segment = new Segment();
			segment.start = node;
		}
		path.length = path.segments.size();
		innerList.add(objectMapper.writeValueAsString(path));
	}

	@Nullable
	private Relationship getRelationshipFromGraphEntries(Data data, long id) {
		return data.graph.relationships.stream().filter(relationship -> Objects.equals(relationship.id, id)).findFirst().orElse(null);
	}

	@Nullable
	private Node getNodeFromGraphEntries(Data data, long id) {
		return data.graph.nodes.stream().filter(node -> Objects.equals(node.id, id)).findFirst().orElse(null);
	}

	public String build() {
		StringBuilder msg;
		if (columnsList.isEmpty()) {
			msg = new StringBuilder();
		} else {
			msg = new StringBuilder(TABLE);
			msg.append(NEW_LINE);
			msg.append(String.join(TAB, columnsList));
			msg.append(NEW_LINE);
			for (List<String> line : linesList) {
				if (line.size() < columnsList.size()) {
					for (int i = line.size(); i < columnsList.size(); i++) {
						line.add(null);
					}
				}
				msg.append(String.join(TAB, line));
				msg.append(NEW_LINE);
			}
		}
		//return new InterpreterResult(Code.SUCCESS, msg.toString());
		return msg.toString();
	}
}
