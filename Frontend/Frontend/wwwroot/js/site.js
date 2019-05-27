var ID_BUTTON = "#grammarEnteredButton";
var ID_TEXTAREA = 'textarea#enteredGrammar';
var PROCESS_GRAMMAR_URL ="http://127.0.0.1:5000/api/values/";

$(document).ready(function(){
    var data = processGrammar();
});

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
                ParseEnteredGrammar(data);
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


function ParseEnteredGrammar() {

}


