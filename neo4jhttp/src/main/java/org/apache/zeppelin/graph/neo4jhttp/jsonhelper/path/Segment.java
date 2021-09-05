package org.apache.zeppelin.graph.neo4jhttp.jsonhelper.path;

import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Node;
import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Relationship;

public class Segment {
	public Node start;
	public Relationship relationship;
	public Node end;
}
