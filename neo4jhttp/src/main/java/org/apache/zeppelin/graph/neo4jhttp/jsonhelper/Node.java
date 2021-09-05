package org.apache.zeppelin.graph.neo4jhttp.jsonhelper;

import java.util.Map;
import java.util.Set;

public class Node {
	//json String but always a number in neo4j
    public Long id;
    public Set<String> labels;
    public Map<String, Object> properties;
}
