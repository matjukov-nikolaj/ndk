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
});


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