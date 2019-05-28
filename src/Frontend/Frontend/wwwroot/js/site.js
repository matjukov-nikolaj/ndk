var ID_BUTTON = "#grammarEnteredButton";
var ID_SEQUENCE_CHECK_BUTTON = "#inputSequenceButton";
var ID_TEXTAREA = 'textarea#enteredGrammar';
var PROCESS_GRAMMAR_URL ="http://127.0.0.1:5000/api/values/";
var PROCESS_SEQUENCE_URL ="http://127.0.0.1:5000/api/sequence/";
var INPUT_HIDDEN_ID ="#sequenceId";
var INPUT_SEQUENCE_ID ="#inputSequence";

$(document).ready(function(){
    processGrammar();
    processSequence();
});

function processSequence() {
    $(ID_SEQUENCE_CHECK_BUTTON).on("click", function() {
        $id = $(INPUT_HIDDEN_ID).val();
        if ($id == null)
        {
            console.log('check sequence id is here');
        }
        else
        {
            $sequence = $(INPUT_SEQUENCE_ID).val();
            $obj = {
                "id" : $id,
                "sequence" : $sequence
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
                success: function(data){
                    SequenceVisualization(data);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    alert(jqXHR.status);
                    alert(textStatus);
                    alert(errorThrown);
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
    CreateString(sequence, '#inputSequenceP');
    $('#inputSequenceP').val(sequence);
    ParseProcessTable(processTable);
    CreateString(result, '#result');
}

function processGrammar() {
    $(ID_BUTTON).on("click", function(){
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
            success: function(data){
                GrammarVisualization(data);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert(jqXHR.status);
                alert(textStatus);
                alert(errorThrown);
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

function CreateString(list, id) {
    var row = '<p>' + list + '</p>';
    $(id).html(row);
}