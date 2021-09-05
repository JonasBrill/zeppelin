ace.define("ace/mode/cypher_highlight_rules",[], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;


var CypherHighlightRules = function() {

    var keywords = (
        "CALL|CREATE|DELETE|DETACH|EXISTS|FOREACH|LOAD|MATCH|MERGE|OPTIONAL|REMOVE|RETURN|SET|START|UNION|UNWIND|WITH"
        + "|LIMIT|ORDER|SKIP|WHERE|YIELD" // Subclauses
        + "|ASC|ASCENDING|ASSERT|BY|CSV|DESC|DESCENDING|ON" // Modifiers
        + "|ALL|CASE|ELSE|END|THEN|WHEN" // Expressions
        + "|AND|AS|CONTAINS|DISTINCT|ENDS|IN|IS|NOT|OR|STARTS|XOR" // Operators
        + "|CONSTRAINT|CREATE|DROP|EXISTS|INDEX|NODE|KEY|UNIQUE" // Schema
        + "|INDEX|JOIN|PERIODIC|COMMIT|SCAN|USING" // Hints
    );

    var builtinConstants = (
        "true|false|null"
    );

    var builtinFunctions = (
        "AND|AS|CONTAINS|DISTINCT|ENDS|IN|IS|NOT|OR|STARTS|XOR"
    );

    var dataTypes = (
        "number|integer|float|string|boolean|point|date|time|localtime|datetime|localdatetime|duration|node|relationship|path"
    );

    var keywordMapper = this.createKeywordMapper({
        "support.function": builtinFunctions,
        "keyword": keywords,
        "constant.language": builtinConstants,
        "storage.type": dataTypes
    }, "identifier", true);

    this.$rules = {
        "start" : [ {
            token : "comment",
            regex : "\\/\\/.*$"
        }, {
            token : "string",           // " string
            regex : '".*?"'
        }, {
            token : "string",           // ' string
            regex : "'.*?'"
        }, {
            token : "string",           // ` string (apache drill)
            regex : "`.*?`"
        }, {
            token : "constant.numeric", // float
            regex : "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"
        }, {
            token : "constant.language.boolean",
            regex : "(?:true|false)\\b"
        }, {
            token : keywordMapper,
            regex : "[a-zA-Z_$][a-zA-Z0-9_$]*\\b"
        }, {
            token : "keyword.operator",
            regex : "\\+|\\-|\\*|\\/|%|\\^|<=|=>|!=|<>|="
        }, {
            token : "punctuation.operator",
            regex : "\\."
        }, {
            token : "paren.lparen",
            regex : /[\[({]/,
            next  : "start"
        }, {
            token : "paren.rparen",
            regex : /[\])}]/
        }, {
            token : "text",
            regex : "\\s+"
        } ]
    };
    this.normalizeRules();
};

oop.inherits(CypherHighlightRules, TextHighlightRules);

exports.CypherHighlightRules = CypherHighlightRules;
});

ace.define("ace/mode/cypher",[], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var CypherHighlightRules = require("./cypher_highlight_rules").CypherHighlightRules;

var Mode = function() {
    this.HighlightRules = CypherHighlightRules;
    this.$behaviour = this.$defaultBehaviour;
};
oop.inherits(Mode, TextMode);

(function() {

    this.lineCommentStart = "--";

    this.$id = "ace/mode/cypher";
}).call(Mode.prototype);

exports.Mode = Mode;

});
                (function() {
                    ace.require(["ace/mode/cypher"], function(m) {
                        if (typeof module == "object") {
                            module.exports = m;
                        }
                    });
                })();
            