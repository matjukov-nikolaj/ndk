var ID_BUTTON = "#grammarEnteredButton";
var ID_STATISTIC_BUTTON = "#statisticMenu";
var ID_SYNTACTICAL_ANALYZER_BUTTON = "#syntacticalAnalyzerMenu";
var ID_SEQUENCE_CHECK_BUTTON = "#inputSequenceButton";
var ID_TEXTAREA = 'textarea#enteredGrammar';
var PROCESS_GRAMMAR_URL = "http://127.0.0.1:5000/api/values/";
var PROCESS_SEQUENCE_URL = "http://127.0.0.1:5000/api/sequence/";
var STATISTIC_URL = "http://127.0.0.1:5000/api/statistic/statistic";
var INPUT_HIDDEN_ID = "#sequenceId";
var INPUT_SEQUENCE_ID = "#inputSequence";
var HIDDEN_CONTENT_ID = "#hiddenContent";
var HIDDEN_SEQUENCE_ID = "#hiddenSequenceContent";
var SYNTACTICAL_ANALYZER_CONTENT_ID = "#syntacticalAnalyzer";
var STATISTIC_CONTENT_ID = "#statisticContent";

$(document).ready(function () {
    $(HIDDEN_CONTENT_ID).hide();
    $(HIDDEN_SEQUENCE_ID).hide();
    $(STATISTIC_CONTENT_ID).hide();
    $(ID_SYNTACTICAL_ANALYZER_BUTTON).on("click", function () {
        $(STATISTIC_CONTENT_ID).hide();
        $(SYNTACTICAL_ANALYZER_CONTENT_ID).show();
    });
    processGrammar();
    processSequence();
    processStatistic();
    var grammar = new Grammar('Z -> S\nS -> S a\nS -> b');
    
    
});
/*
 *  The MIT License
 * 
 *  Copyright 2011 Greg.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

function extend(objekt, zuper) {
    _.extend(objekt, zuper);

    objekt.zuper = zuper;
}

function newObject(prototype) {
    function F() {
        // Deliberatley left empty
    }

    F.prototype = prototype;

    return new F();
}

function includes(array1, array2) {
    for (var i in array1) {
        if (array2.indexOf(array1[i]) < 0) {
            return false;
        }
    }

    return true;
}

function includeEachOther(array1, array2) {
    return includes(array1, array2) && includes(array2, array1);
}

function includesUsingEquals(array1, array2) {
    for (var i in array1) {
        if (indexOfUsingEquals(array1[i], array2) < 0) {
            return false;
        }
    }

    return true;
}

function includeEachOtherUsingEquals(array1, array2) {
    return includesUsingEquals(array1, array2) && includesUsingEquals(array2, array1);
}

function getOrCreateArray(dictionary, key) {
    var result = dictionary[key];

    if (result == undefined) {
        result = [];
        dictionary[key] = result;
    }

    return result;
}

/**
 * @return
 * <br>Array
 * <br>New
 */
function trimElements(array) {
    var result = [];

    for (var i in array) {
        result[i] = array[i].trim();
    }

    return result;
}

function isElement(element, array) {
    for (var i in array) {
        if (element == array[i]) {
            return true;
        }
    }

    return false;
}

/**
 * @param array
 * <br>Input-output
 * @return <code>true</code> iff <code>array</code> has been modified
 */
function addUnique(element, array) {
    if (!isElement(element, array)) {
        array.push(element);

        return true;
    }

    return false;
}

function isElementUsingEquals(element, array) {
    for (var i in array) {
        if (element.equals(array[i])) {
            return true;
        }
    }

    return false;
}

/**
 * @param array
 * <br>Input-output
 * @return <code>true</code> iff <code>array</code> has been modified
 */
function addUniqueUsingEquals(element, array) {
    if (!isElementUsingEquals(element, array)) {
        array.push(element);

        return true;
    }

    return false;
}

/**
 * @return
 * <br>Range: <code>[-1 .. array.length - 1]</code>
 */
function indexOfUsingEquals(element, array) {
    for (var i in array) {
        if (element.equals(array[i])) {
            return i;
        }
    }

    return -1;
}

function $element(id) {
    return document.getElementById(id);
}

function assertEquality(expected, actual) {
    if (expected != actual) {
        throw 'Assertion failed: expected ' + expected + ' but was ' + actual;
    }
}

function assertEquals(expected, actual) {
    if (!expected.equals(actual)) {
        throw 'Assertion failed: expected ' + expected + ' but was ' + actual;
    }
}

function resize(textInput, minimumSize) {
    textInput.size = Math.max(minimumSize, textInput.value.length);
}

/*
 *  The MIT License
 * 
 *  Copyright 2011 Greg.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

var EPSILON = '\'\'';

function Grammar(text) {
    // <PUBLIC>

    this.alphabet = [];
    this.nonterminals = [];
    this.terminals = [];
    this.rules = [];
    this.firsts = new Object();
    this.follows = new Object();

    this.toString = function() {
        return this.rules.join('\n');
    };

    this.getRulesForNonterminal = function(nonterminal) {
        var result = [];

        for (var i in this.rules) {
            var rule = this.rules[i];

            if (nonterminal == rule.nonterminal) {
                result.push(rule);
            }
        }

        return result;
    };

    /**
     * @param sequence
     * <br>Array of symbols
     * @result
     * <br>Array of terminal symbols
     * <br>New
     */
    this.getSequenceFirsts = function(sequence) {
        var result = [];
        var epsilonInSymbolFirsts = true;

        for (var j in sequence) {
            var symbol = sequence[j];
            epsilonInSymbolFirsts = false;

            if (isElement(symbol, this.terminals)) {
                addUnique(symbol, result);

                break;
            }

            for (var k in this.firsts[symbol]) {
                var first = this.firsts[symbol][k];

                epsilonInSymbolFirsts |= first == EPSILON;

                addUnique(first, result);
            }

            epsilonInSymbolFirsts |= this.firsts[symbol] == undefined || this.firsts[symbol].length == 0;

            if (!epsilonInSymbolFirsts) {
                break;
            }
        }

        if (epsilonInSymbolFirsts) {
            addUnique(EPSILON, result);
        }

        return result;
    };

    // </PUBLIC>

    // <INITIALIZATION>

    initializeRulesAndAlphabetAndNonterminals(this);
    initializeAlphabetAndTerminals(this);
    initializeFirsts(this);
    initializeFollows(this);

    // </INITIALIZATION>

    // <PRIVATE>

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeRulesAndAlphabetAndNonterminals(grammar) {
        var lines = text.split('\n');

        for (var i in lines) {
            var line = lines[i].trim();

            if (line != '') {
                var rule = new Rule(grammar, line);

                grammar.rules.push(rule);

                if (grammar.axiom == undefined) {
                    grammar.axiom = rule.nonterminal;
                }

                addUnique(rule.nonterminal, grammar.alphabet);
                addUnique(rule.nonterminal, grammar.nonterminals);
            }
        }
    }

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeAlphabetAndTerminals(grammar) {
        for (var i in grammar.rules) {
            var rule = grammar.rules[i];

            for (var j in rule.development) {
                var symbol = rule.development[j];

                if (symbol != EPSILON && !isElement(symbol, grammar.nonterminals)) {
                    addUnique(symbol, grammar.alphabet);
                    addUnique(symbol, grammar.terminals);
                }
            }
        }
    }

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeFirsts(grammar) {
        var notDone;

        do {
            notDone = false;

            for (var i in grammar.rules) {
                var rule = grammar.rules[i];
                var nonterminalFirsts = getOrCreateArray(grammar.firsts, rule.nonterminal);

                if (rule.development.length == 1 && rule.development[0] == EPSILON) {
                    notDone |= addUnique(EPSILON, nonterminalFirsts);
                } else {
                    notDone |= collectDevelopmentFirsts(grammar, rule.development, nonterminalFirsts);
                }
            }
        } while (notDone);
    }

    /**
     * @param grammar
     * <br>Input-output
     * @param development
     * <br>Array of symbols
     * @param nonterminalFirsts
     * <br>Array of symbols
     * <br>Input-output
     * @return <code>true</code> If <code>nonterminalFirsts</code> has been modified
     */
    function collectDevelopmentFirsts(grammar, development, nonterminalFirsts) {
        var result = false;
        var epsilonInSymbolFirsts = true;

        for (var j in development) {
            var symbol = development[j];
            epsilonInSymbolFirsts = false;

            if (isElement(symbol, grammar.terminals)) {
                result |= addUnique(symbol, nonterminalFirsts);

                break;
            }

            for (var k in grammar.firsts[symbol]) {
                var first = grammar.firsts[symbol][k];

                epsilonInSymbolFirsts |= first == EPSILON;

                result |= addUnique(first, nonterminalFirsts);
            }

            if (!epsilonInSymbolFirsts) {
                break;
            }
        }

        if (epsilonInSymbolFirsts) {
            result |= addUnique(EPSILON, nonterminalFirsts);
        }

        return result;
    }

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeFollows(grammar) {
        var notDone;

        do {
            notDone = false;

            for (var i in grammar.rules) {
                var rule = grammar.rules[i];

                if (i == 0) {
                    var nonterminalFollows = getOrCreateArray(grammar.follows, rule.nonterminal);

                    notDone |= addUnique('$', nonterminalFollows);
                }

                for (var j in rule.development) {
                    var symbol = rule.development[j];

                    if (isElement(symbol, grammar.nonterminals)) {
                        var symbolFollows = getOrCreateArray(grammar.follows, symbol);
                        var afterSymbolFirsts = grammar.getSequenceFirsts(rule.development.slice(parseInt(j) + 1));

                        for (var k in afterSymbolFirsts) {
                            var first = afterSymbolFirsts[k];

                            if (first == EPSILON) {
                                var nonterminalFollows = grammar.follows[rule.nonterminal];

                                for (var l in nonterminalFollows) {
                                    notDone |= addUnique(nonterminalFollows[l], symbolFollows);
                                }
                            } else {
                                notDone |= addUnique(first, symbolFollows);
                            }
                        }
                    }
                }
            }
        } while (notDone);
    }

    // </PRIVATE>
}

function Rule(grammar, text) {
    // <PUBLIC>

    this.grammar = grammar;
    this.index = grammar.rules.length;

    var split = text.split('->');

    this.nonterminal = split[0].trim();

    this.pattern = trimElements(this.nonterminal.split(' '));

    this.development = trimElements(split[1].trim().split(' '));

    this.toString = function() {
        return this.nonterminal + ' -> ' + this.development.join(' ');
    };

    this.equals = function(that) {
        if (this.nonterminal != that.nonterminal) {
            return false;
        }

        if (parseInt(this.development.length) != parseInt(that.development.length)) {
            return false;
        }

        for (var i in this.development) {
            if (this.development[i] != that.development[i]) {
                return false;
            }
        }

        return true;
    };

    // </PUBLIC>
}

function BasicItem(rule, dotIndex) {
    // <PUBLIC>

    this.rule = rule;

    this.dotIndex = dotIndex;

    this.lookAheads = [];

    this.addUniqueTo = function(items) {
        return addUniqueUsingEquals(this, items);
    };

    this.newItemsFromSymbolAfterDot = function() {
        var result = [];
        var nonterminalRules = this.rule.grammar.getRulesForNonterminal(this.rule.development[this.dotIndex]);

        for (var j in nonterminalRules) {
            addUniqueUsingEquals(new Item(nonterminalRules[j], 0), result);
        }

        return result;
    };

    this.newItemAfterShift = function() {
        if (this.dotIndex < this.rule.development.length && this.rule.development[this.dotIndex] != EPSILON) {
            return new Item(this.rule, this.dotIndex + 1);
        }

        return undefined;
    }

    this.equals = function(that) {
        return this.rule.equals(that.rule) && (parseInt(this.dotIndex) == parseInt(that.dotIndex));
    };

    this.toString = function() {
        return this.rule.nonterminal + ' -> ' + this.rule.development.slice(0, this.dotIndex).join(' ') + '.' +
            (isElement(EPSILON, this.rule.development) ? '' : this.rule.development.slice(this.dotIndex).join(' '));
    };

    // </PUBLIC>
}

function BasicLR1Item(rule, dotIndex) {
    // <PUBLIC>

    extend(this, new BasicItem(rule, dotIndex));

    var zuper = this.zuper;

    this.lookAheads = rule.index == 0 ? ['$'] : [];

    this.newItemsFromSymbolAfterDot = function() {
        var result = this.zuper.newItemsFromSymbolAfterDot();

        if (result.length == 0) {
            return result;
        }

        var newLookAheads = [];
        var epsilonPresent = false;
        var firstsAfterSymbolAfterDot = this.rule.grammar.getSequenceFirsts(this.rule.development.slice(this.dotIndex + 1));

        for (var i in firstsAfterSymbolAfterDot) {
            var first = firstsAfterSymbolAfterDot[i];
            if (EPSILON == first) {
                epsilonPresent = true;
            } else {
                addUnique(first, newLookAheads);
            }
        }

        if (epsilonPresent) {
            for (var i in this.lookAheads) {
                addUnique(this.lookAheads[i], newLookAheads);
            }
        }

        for (var i in result) {
            result[i].lookAheads = newLookAheads.slice(0);
        }

        return result;
    };

    this.newItemAfterShift = function() {
        var result = zuper.newItemAfterShift();

        if (result != undefined) {
            result.lookAheads = this.lookAheads.slice(0);
        }

        return result;
    }

    this.addUniqueTo = function(items) {
        var result = false;

        for (var i in items) {
            var item = items[i];

            if (zuper.equals(item)) {
                for (var i in this.lookAheads) {
                    result |= addUnique(this.lookAheads[i], item.lookAheads);
                }

                return result;
            }
        }

        items.push(this);

        return true;
    };

    this.equals = function(that) {
        return zuper.equals(that) && includeEachOther(this.lookAheads, that.lookAheads);
    }

    this.toString = function() {
        return '[' + zuper.toString() + ', ' + this.lookAheads.join('/') + ']';
    };

    // </PUBLIC>
}



function processStatistic() {
    $(ID_STATISTIC_BUTTON).on("click", function (event) {
        event.preventDefault();
        $(SYNTACTICAL_ANALYZER_CONTENT_ID).hide();
        $(STATISTIC_CONTENT_ID).show();
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: STATISTIC_URL,
            type: "GET",
            async: true,
            cors: true,
            success: function (data) {
                StatisticVisualization(data);
                $(HIDDEN_SEQUENCE_ID).show();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                ErrorVisualization(jqXHR, textStatus, errorThrown, '#error');
            },
            dataType: "json"
        });
    });
}

function StatisticVisualization(data) {
    var statistic = ParseJson(data);
    var result = statistic.result;
    ParseStatistic(result);
}

function processSequence() {
    $(ID_SEQUENCE_CHECK_BUTTON).on("click", function () {
        $id = $(INPUT_HIDDEN_ID).val();
        if ($id == null) {
            console.log('check sequence id is here');
        } else {
            $sequence = $(INPUT_SEQUENCE_ID).val();
            $obj = {
                "id": $id,
                "sequence": $sequence
            };

            $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                url: PROCESS_SEQUENCE_URL,
                type: "POST",
                async: true,
                cors: true,
                data: JSON.stringify($obj),
                success: function (data) {
                    SequenceVisualization(data);
                    $(HIDDEN_SEQUENCE_ID).show();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    ErrorVisualization(jqXHR, textStatus, errorThrown, '#error');
                },
                dataType: "json"
            });

        }
    });
}


function SequenceVisualization(data) {
    var parseInputSequence = ParseJson(data);
    var sequence = parseInputSequence.sequence;
    var processTable = parseInputSequence.processTable;
    var result = parseInputSequence.result;
    $('#inputSequenceId').text(sequence);
    ParseProcessTable(processTable);
    var resultText = "sequence " + (result ? "accepted" : "declined");
    var resultClass = (result ? "text-accepted" : "text-declined");
    $('#algorithmId').text(resultText).removeClass().addClass(resultClass);
}

function processGrammar() {
    $(ID_BUTTON).on("click", function () {
        var message = $(ID_TEXTAREA).val();
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: PROCESS_GRAMMAR_URL,
            type: "POST",
            async: false,
            cors: true,
            data: JSON.stringify(message),
            success: function (data) {
                GrammarVisualization(data);
                $(HIDDEN_CONTENT_ID).show();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                ErrorVisualization(jqXHR, textStatus, errorThrown, '#error');
                CreateString(jqXHR.status + ", " + textStatus + ", " + errorThrown + '<br><p>Sorry, please try again</p>', "#errorBody");
                $('#error').modal('show');
            },
            dataType: "json"
        });
    });
}

function GrammarVisualization(data) {
    var enteredGrammar = ParseJson(data);
    var id = enteredGrammar.id;
    $(INPUT_HIDDEN_ID).val(id);
    var grammar = enteredGrammar.grammar;
    var newGrammar = enteredGrammar.newGrammar;
    var table = enteredGrammar.table;
    ParseGrammar(grammar);
    LeftRecursionDeletion(newGrammar);
    TableM(table);
}

function ParseJson(json) {
    json = JSON.parse(json);
    return json;
}

function ParseGrammar(grammar) {
    grammar = ParseJson(grammar);
    var productions = grammar.productions;
    var terminals = grammar.terminals;
    var noTerminals = grammar.noTerminals;
    var startSymbol = grammar.startSymbol;

    CreateEnteredGrammarTable(productions, '#grammar');
    CreateEnteredGrammarTable(terminals, '#terminals');
    CreateEnteredGrammarTable(noTerminals, '#noTerminals');
    CreateEnteredGrammarTable(startSymbol, '#startSymbol');
}

function LeftRecursionDeletion(newGrammar) {
    newGrammar = ParseJson(newGrammar);
    var productions = newGrammar.productions;
    var first = newGrammar.first;
    var follow = newGrammar.follow;
    var terminals = newGrammar.terminals;
    var noTerminals = newGrammar.noTerminals;

    CreateNewGrammarTable(productions, first, follow, '#leftRecursionDeletion');
}

function ParseProcessTable(processTable) {
    var state = processTable.STATE;
    var sequence = processTable.SEQUENCE;
    var transition = processTable.TRANSITION;

    CreateEnteredGrammarTable(state, '#state');
    CreateEnteredGrammarTable(sequence, '#sequence');
    CreateEnteredGrammarTable(transition, '#transition');
}


function ParseStatistic(statistic) {
    var accepted = statistic[0];
    var declined = statistic[1];

    var valueAccepted = accepted.Value;
    var valueDeclined = declined.Value;
    ParseStatisticItems(valueAccepted, "#acceptedItems", "grammarAccepted", "sequenceAccepted");
    ParseStatisticItems(valueDeclined, "#declinedItems", "grammarDeclined", "sequenceDeclined");
}

function ParseStatisticItems(items, idItems, idGrammar, idSequence) {
    var row = '';
    for (var item in items) {
        var key = items[item].Key;
        var value = items[item].Value;
        row += CreateAcceptedStatisticTable(key, row, item, idGrammar, idSequence);
        $(idItems).html(row);

    }
    for (var item in items) {
        var key = items[item].Key;
        var value = items[item].Value;
        CreateStatisticGrammarTable(key, idGrammar + item);
        CreateStatisticSequenceTable(value, idSequence + item);
    }
}


function TableM(table) {
    table = ParseJson(table);
    var mTable = table.mTable;

    CreateMTableTable(mTable, '#mTable');
}

function CreateEnteredGrammarTable(list, id) {
    var row = '';
    for (var i in list) {
        row += '<p>' + escapeHtml(list[i]) + '</p>';
    }
    $(id).html(row);
}


function CreateStatisticGrammarTable(list, id) {
    var row = '';
    for (var i in list) {
        row += '<p>' + escapeHtml(list[i]) + '</p>';
    }
    console.log(id);
    $("#" + id).html(row);
}

function CreateAcceptedStatisticTable(items, row, number, idGrammarItem, idSequenceItem) {
    row = ' <div class="card-container d-flex flex-row mt-3">\n' +
        '                    <div class="card card-grammar">\n' +
        '                        <div class="card-header">Grammar</div>\n' +
            '                        <div class="card-body scrollbar-near-moon" id="' + idGrammarItem + number + '">\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                    <div class="card card-sequence">\n' +
        '                        <div class="card-header">Sequences</div>\n' +
        '                        <div class="card-body scrollbar-near-moon" id="' + idSequenceItem + number + '">\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                </div>\n';
    return row;
}

function CreateStatisticSequenceTable(items, id) {
    var row = '';
    for (var i in items) {
        row += '<p>' + escapeHtml(items[i]) + '</p><hr/>';
    }
    console.log(row);
    $("#" + id).html(row);
}

function CreateNewGrammarTable(productions, first, follow, id) {
    var row = '';
    for (var i in productions) {
        row += '<tr><td>' +

            escapeHtml(productions[i]) + '</td><td>' + escapeHtml(first[i]) + '</td><td>' + escapeHtml(follow[i]) + '</td></tr>';
    }
    $(id).html(row);
}

function CreateMTableTable(mTable, id) {
    var row = '';
    for (var i in mTable) {
        (i == 0) ? row += '<tr class="card-header">' : row += '<tr>';
        for (var j in mTable[i]) {
            (j == 0) ? row += '<td class="card-header">' + escapeHtml(mTable[i][j]) + '</td>' : row += '<td>' + escapeHtml(mTable[i][j]) + '</td>';
        }
        row += '</tr>';
    }
    $(id).html(row);
}

function ErrorVisualization(jqXHR, textStatus, errorThrown, id) {
    CreateString(jqXHR.status + ", " + textStatus + ", " + errorThrown + '<br><p>Sorry, please try again</p>', "#errorBody");
    $(id).modal('show');
}

function CreateString(list, id) {
    var row = '<p>' + escapeHtml(list) + '</p>';
    $(id).html(row);
}

var entityMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;',
    '`': '&#x60;',
    '=': '&#x3D;'
};

function escapeHtml (string) {
    return String(string).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });
}
