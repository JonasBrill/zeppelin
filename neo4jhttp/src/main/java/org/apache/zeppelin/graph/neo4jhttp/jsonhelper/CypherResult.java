package org.apache.zeppelin.graph.neo4jhttp.jsonhelper;

import java.util.List;

public class CypherResult {

    public List<Result> results;
    public List<Error> errors;

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public boolean isMultiResult() {
        return results.size() != 1;
    }

    public String getFirstErrorMessage() {
        return errors.get(0).message;
    }
}
