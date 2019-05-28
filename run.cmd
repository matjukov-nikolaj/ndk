start redis-server.exe

start "Frontend" /d Frontend\ dotnet Frontend.dll

start "Backend" /d Backend\ dotnet Backend.dll

start "GrammarConverter" /d GrammarConverter\ dotnet GrammarConverter.dll

start "GrammarProcessor" /d GrammarProcessor\ dotnet GrammarProcessor.dll

start "SequenceHandler" /d SequenceHandler\ dotnet SequenceHandler.dll

start "TableMGenerator" /d TableMGenerator\ dotnet TableMGenerator.dll
