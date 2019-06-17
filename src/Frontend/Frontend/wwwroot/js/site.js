var ID_BUTTON = "#grammarEnteredButton";
var ID_BUTTON_SLR = "#grammarEnteredButtonSlr";
var ID_STATISTIC_BUTTON = "#statisticMenu";
var ID_SHOW_PROCESS_BUTTON = "#showProcess";
var ID_PROCESS_SEQUENCE_BUTTON = "#slrInputSequenceButton";
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
var SHOW_PROCESS_CONTENT_ID = "#showProcessContent";

$(document).ready(function () {
    $(HIDDEN_CONTENT_ID).hide();
    $(HIDDEN_SEQUENCE_ID).hide();
    $(STATISTIC_CONTENT_ID).hide();
    $(SHOW_PROCESS_CONTENT_ID).hide();
    $(ID_SYNTACTICAL_ANALYZER_BUTTON).on("click", function () {
        $(STATISTIC_CONTENT_ID).hide();
        $(SYNTACTICAL_ANALYZER_CONTENT_ID).show();
    });
    processSlrGrammar();
    processGrammar();
    processSequence();
    processStatistic();
});

function processSlrGrammar() {
    $(ID_BUTTON_SLR).on("click", function () {
        var message = $(ID_TEXTAREA).val();
        // var grammar = new Grammar(message);
        var grammar = new Grammar("S -> a A\n" +
            "A -> B , A\n" +
            "A -> B\n" +
            "B -> b\n" +
            "B -> c\n" +
            "B -> d");
        var lrClosureTable = new LRClosureTable(grammar);
        var lrTable = new LRTable(lrClosureTable);
        console.log(grammar);
        GrammarSlrVisualization(grammar);
        ShowProcess(lrClosureTable);
        ProcessSlrTable(lrTable);
        ProcessSequence(lrTable);
    });

}

function ProcessSequence(lrTable) {
    $(ID_PROCESS_SEQUENCE_BUTTON).on("click", function () {
        parseInput(lrTable);
    });
}


function parseInput(lrTable) {
    var stack = [0];

    function stateIndex() {
        return stack[2 * ((stack.length - 1) >> 1)];
    }

    var tokens = ($element('slrInputSequence').value.trim() + ' $').split(' ');
    // var maximumStepCount = parseInt($element('slrInputSequence').value);
    var tokenIndex = 0;
    var token = tokens[tokenIndex];
    var state = lrTable.states[stateIndex()];
    var action = state[token];
    var actionElement = chooseActionElement(state, token);
    // var rows = "<tr><td>1</td><td>" + formatStack(stack) + "</td><td>" + tokens.slice(tokenIndex).join(' ') + "</td><td>" + formatAction(state, token, false) + "</td><td id=\"tree\" style=\"vertical-align: top;\"></td></tr>\n";
    var i = 2;

    while (action != undefined && actionElement != 'r0') {
        if (actionElement.actionType == 's') {
            stack.push(tokens[tokenIndex++]);
            stack.push(parseInt(actionElement.actionValue));
        } else if (actionElement.actionType == 'r') {
            var ruleIndex = actionElement.actionValue;
            var rule = lrTable.grammar.rules[ruleIndex];
            var removeCount = isElement(EPSILON, rule.development) ? 0 : rule.development.length * 2;
            var removedElements = stack.splice(stack.length - removeCount, removeCount);
            var node = new Tree(rule.nonterminal, []);

            for (var j = 0; j < removedElements.length; j += 2) {
                node.children.push(removedElements[j]);
            }

            stack.push(node);
        } else {
            stack.push(parseInt(actionElement));
        }

        var state = lrTable.states[stateIndex()];
        var token = stack.length % 2 == 0 ? stack[stack.length - 1] : tokens[tokenIndex];
        action = state[token];
        actionElement = chooseActionElement(state, token);

        // rows += "<tr><td>" + i + "</td><td>" + formatStack(stack) + "</td><td>" + tokens.slice(tokenIndex).join(' ') + "</td><td>" + formatAction(state, token, false) + "</td></tr>\n";

        // console.log(stack);
        console.log(token);
        ++i;
        
    }

}

function ProcessSlrTable(lrTable) {
    var grammar = lrTable.grammar.alphabet;
    var row = '';
    row += '<tr class="card-header">';
    row += '<td></td>';
    for (var i in grammar) {
        for (var j in grammar[i]) {
            (j == 0) ? row += '<td class="card-header">' + grammar[i][j] + '</td>' : row += '<td>' + grammar[i][j] + '</td>';
        }
    }
    row += '<td class="card-header">$</td>';
    row += '</tr>';

    var grammar = lrTable.grammar.alphabet;
    grammar.push("$");
    lrTable.states.forEach(function (state) {
        row += '<tr>';
        row += '<td class="card-header">' + state.index + '</td>';
        var i = 0;
        grammar.forEach(function (elem) {
            if (state[elem] !== undefined) {
                row += '<td>' + ((state[elem][0].actionType == "r") ? "r" : "") + state[elem][0].actionValue + '</td>';
            } else {
                row += '<td>' + '</td>';
            }
            ++i;
        });
    });
    $("#slrTable").html(row);
}

function ShowProcess(lrClosureTable) {
    $(ID_SHOW_PROCESS_BUTTON).on("click", function () {
        ($(SHOW_PROCESS_CONTENT_ID).css('display') == 'none') ? $(SHOW_PROCESS_CONTENT_ID).show() : $(SHOW_PROCESS_CONTENT_ID).hide();
        GetDataForShowProcessTable(lrClosureTable);
    });
}

function GetDataForShowProcessTable(lrClosureTable) {
    var states = [];
    var transitions = [];
    var curPoses = [];
    var lastPoses = [];
    states.push(0);
    transitions.push([]);
    var items = lrClosureTable.kernels[0].items;
    var tmpStr2 = "";
    items.forEach(function (kernel) {
        tmpStr2 += kernel.rule.pattern + " -> " + kernel.rule.development + "    ";
    });
    curPoses.push(tmpStr2);
    lrClosureTable.kernels.forEach(function (elem) {

        Object.values(elem.gotos).forEach(function (gotos) {
            var tmpStr2 = "";
            lrClosureTable.kernels[gotos.toString()].items.forEach(function (kernel) {
                tmpStr2 += kernel.rule.pattern + " -> " + kernel.rule.development + "    ";
            });
            curPoses.push(tmpStr2);
        });

        if (elem.keys.length !== 0) {
            elem.keys.forEach(function (key) {
                var str = elem.index + ": " + key.toString();
                transitions.push(str);
            });
            Object.values(elem.gotos).forEach(function (gotos) {
                var str = gotos.toString();
                states.push(str);
            });
        }

        var tmpStr = "";
        elem.closure.forEach(function (rule) {
            Object.values(rule).forEach(function (rule2) {
                if (typeof rule2.pattern != "undefined") {
                    tmpStr += rule2.pattern + " -> " + rule2.development + "    ";
                }
            });
        });
        lastPoses.push(tmpStr);
    });

    CreateShowProcessTable(states, transitions, curPoses, lastPoses, "#showProcessTable");
}

function GrammarSlrVisualization(grammar) {

    $("#startSymbolSlr").html(grammar.axiom);
    CreateEnteredGrammarTable(CreateSlrList(grammar.rules), "#grammarSlr");
    CreateEnteredGrammarTable(grammar.terminals, "#terminalsSlr");
    CreateEnteredGrammarTable(grammar.nonterminals, "#noTerminalsSlr");

    CreateSlrFirstFollowTable(grammar.firsts, grammar.follows, '#slrFirstFollow');
}


function CreateSlrFirstFollowTable(first, follow, id) {
    var row = '';
    for (var i in first) {
        row += '<tr><td>' +

            i + '</td><td>' + first[i] + '</td><td>' + follow[i] + '</td></tr>';
    }

    $(id).html(row);
}

function CreateSlrList(rules) {
    var list = [];
    rules.forEach(function (rule) {
        var str = rule.pattern[0] + " -> " + rule.development[0];
        list.push(str);
    });

    return list;
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
        row += '<p>' + list[i] + '</p>';
    }
    $(id).html(row);
}


function CreateStatisticGrammarTable(list, id) {
    var row = '';
    for (var i in list) {
        row += '<p>' + list[i] + '</p>';
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
        row += '<p>' + items[i] + '</p><hr/>';
    }
    console.log(row);
    $("#" + id).html(row);
}

function CreateNewGrammarTable(productions, first, follow, id) {
    var row = '';
    for (var i in productions) {
        row += '<tr><td>' +

            productions[i] + '</td><td>' + first[i] + '</td><td>' + follow[i] + '</td></tr>';
    }
    $(id).html(row);
}

function CreateShowProcessTable(states, transitions, curPoses, lastPoses, id) {
    var row = '';
    for (var i in states) {
        row += '<tr><td>' +

            states[i] + '</td><td>' + transitions[i] + '</td><td>' + curPoses[i] + '</td><td>' + lastPoses[i] + '</td></tr>';
    }
    $(id).html(row);
}

function CreateMTableTable(mTable, id) {
    var row = '';
    for (var i in mTable) {
        (i == 0) ? row += '<tr class="card-header">' : row += '<tr>';
        for (var j in mTable[i]) {
            (j == 0) ? row += '<td class="card-header">' + mTable[i][j] + '</td>' : row += '<td>' + mTable[i][j] + '</td>';
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
    var row = '<p>' + list + '</p>';
    $(id).html(row);
}