var ID_BUTTON = "#grammarEnteredButton";
var ID_BUTTON_SLR = "#grammarEnteredButtonSlr";
var ID_STATISTIC_BUTTON = "#statisticMenu";
var ID_SHOW_PROCESS_BUTTON = "#showProcess";
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
        // var grammar = new Grammar("A -> A a B\n" +
        //     "A -> B\n" +
        //     "B -> B k C\n" +
        //     "B -> C\n" +
        //     "C -> y\n" +
        //     "C -> x C\n" +
        //     "C -> n D i\n" +
        //     "D -> ''\n" +
        //     "D -> o A z");
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
    });

}


function ShowProcess(lrClosureTable) {
    $(ID_SHOW_PROCESS_BUTTON).on("click", function () {
        $(SHOW_PROCESS_CONTENT_ID).show();
        var states = [];
        var transitions = [];
        var curPoses = [];
        var lastPoses = [];
        states.push(0);
        transitions.push([]);
        var chtoto = Object.values(lrClosureTable.kernels[0].gotos).items;
        var tmpStr2 = "";
        chtoto.forEach(function (kernel) {
            tmpStr2 += kernel.rule.pattern + " -> " + kernel.rule.development + "    ";
        });
        curPoses.push(tmpStr2);
        lrClosureTable.kernels.forEach(function(elem) {
            
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

            // var tmpStr2 = "";
            // elem.items.forEach(function (kernel) {
                // Object.values(kernel.rule).forEach(function(rule2) {
                //     if (typeof kernel.rule.pattern != "undefined") {
                //         tmpStr2 += kernel.rule.pattern + " -> " + kernel.rule.development + "    ";
                //     }
                // });
            // });

            // curPoses.push(tmpStr2);
            
            var tmpStr = "";
            elem.closure.forEach(function (rule) {
                // CreateSlrList(rules);
                Object.values(rule).forEach(function(rule2) {
                    if (typeof rule2.pattern != "undefined") {
                        tmpStr += rule2.pattern + " -> " + rule2.development + "    ";
                    }
                });
                // transitions.push(str);
            });
            lastPoses.push(tmpStr);
        });
        CreateShowProcessTable(states, transitions, curPoses, lastPoses, "#showProcessTable");
    });
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
    rules.forEach(function(rule) {
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