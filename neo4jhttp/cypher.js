ace.define("ace/snippets/cypher",[], function(require, exports, module) {
"use strict";

exports.snippetText = "snippet bm\n\
	MATCH (n)-[m]->(l) RETURN *;\n\
snippet am\n\
	MATCH (n)-[m]->(l) WHERE n.label = m.label RETURN *;\n\
";

exports.scope = "cypher";

});
                (function() {
                    ace.require(["ace/snippets/cypher"], function(m) {
                        if (typeof module == "object") {
                            module.exports = m;
                        }
                    });
                })();
