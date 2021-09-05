package org.apache.zeppelin.graph.neo4jhttp.jsonhelper.path;

import org.apache.zeppelin.graph.neo4jhttp.jsonhelper.Node;

import java.util.ArrayList;
import java.util.List;

public class Path {
	public Node start;
	public Node end;
	public List<Segment> segments = new ArrayList<>();
	public long length;
}
