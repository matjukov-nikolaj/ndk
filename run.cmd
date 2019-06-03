start redis-server.exe

start "Frontend" /d Frontend\ dotnet Frontend.dll

start "Backend" /d Backend\ dotnet Backend.dll

start "GrammarProcessor" /d GrammarProcessor\ dotnet GrammarProcessor.dll

set file=%CD%\config\number.txt
for /f "tokens=1,2 delims=:" %%a in (%file%) do (
for /l %%i in (1, 1, %%b) do start "%%a" /d %%a dotnet %%a.dll
)

start "SequenceHandler" /d SequenceHandler\ dotnet SequenceHandler.dll

start "SequenceStatistic" /d SequenceStatistic\ dotnet SequenceStatistic.dll