package org.apache.zeppelin.graph.neo4jhttp;

import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Graph;
import org.apache.zeppelin.interpreter.graph.GraphResult;
import org.apache.zeppelin.tabledata.Node;
import org.apache.zeppelin.tabledata.Relationship;

import java.util.*;

public class GraphResultBuilder {
	private final List<Node> nodesList = new ArrayList<>();
	private final Map<String, String> nodeLabelsMap = new HashMap<>();
	private final List<Relationship> relationshipList = new ArrayList<>();
	private final Set<String> relationshipTypesSet = new HashSet<>();

	//housekeeping
	private final Set<Long> nodeIdSet = new HashSet<>();
	private final Set<Long> relationshipIdSet = new HashSet<>();

	public void addGraph(Graph graph) {
		addNodes(graph.nodes);
		addRelations(graph.relationships);
	}

	private void addNodes(List<org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Node> nodes) {
		for (org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Node n : nodes) {
			long id = n.id;
			if (nodeIdSet.add(id)) {
				Node zeppelinNode = new Node(id, n.properties, n.labels);
				nodesList.add(zeppelinNode);

				for (String l : n.labels){
					//set label color fixed to black while there are possible duplicate labels
					nodeLabelsMap.put(l, "#000000");
				}
			}
		}
	}

	private void addRelations(List<org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Relationship> relationships) {
		for (org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Relationship r : relationships) {
			long id = r.id;
			if (relationshipIdSet.add(id)) {
				Relationship zeppelinRelationship = new Relationship(id, r.properties, r.startNode, r.endNode, r.type);
				relationshipList.add(zeppelinRelationship);

				relationshipTypesSet.add(r.type);
			}
		}
	}

	public GraphResult.Graph build() {
		generateRandomLabelColors();

		return new GraphResult.Graph(nodesList, relationshipList, nodeLabelsMap, relationshipTypesSet, true);
	}

	private void generateRandomLabelColors() {
		Random random = new Random();
		for (Map.Entry<String,String> e: nodeLabelsMap.entrySet()){
			e.setValue(String.format("#%06x", random.nextInt(0xffffff + 1)));
		}
	}
}
