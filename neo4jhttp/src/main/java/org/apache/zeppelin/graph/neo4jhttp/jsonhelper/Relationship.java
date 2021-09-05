package org.apache.zeppelin.graph.neo4jhttp.jsonhelper;

import java.util.Map;

public class Relationship {
	//json String but always a number in neo4j
	public Long id;
	public String type;
	//json String but always a number in neo4j
	public Long startNode;
	//json String but always a number in neo4j
	public Long endNode;
	public Map<String, Object> properties;
}
