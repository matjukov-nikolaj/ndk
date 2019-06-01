cd src/Core/Core
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../Frontend/Frontend
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../Backend/Backend
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../GrammarConverter/GrammarConverter
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../GrammarProcessor/GrammarProcessor
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../SequenceHandler/SequenceHandler
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../TableMGenerator/TableMGenerator
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../SequenceStatistic/SequenceStatistic
start /wait dotnet publish --configuration Release
if %ERRORLEVEL% NEQ 0 (
    goto BuildError
)

cd ../../..
if exist "BUILD" (
    rd /s /q "BUILD"
)

mkdir "BUILD"\Frontend
mkdir "BUILD"\Backend
mkdir "BUILD"\GrammarConverter
mkdir "BUILD"\GrammarProcessor
mkdir "BUILD"\SequenceHandler
mkdir "BUILD"\TableMGenerator
mkdir "BUILD"\SequenceStatistic
mkdir "BUILD"\config

xcopy src\Frontend\Frontend\bin\Release\netcoreapp2.2\publish "BUILD"\Frontend\
xcopy src\Backend\Backend\bin\Release\netcoreapp2.2\publish "BUILD"\Backend\
xcopy src\GrammarConverter\GrammarConverter\bin\Release\netcoreapp2.2\publish "BUILD"\GrammarConverter\
xcopy src\GrammarProcessor\GrammarProcessor\bin\Release\netcoreapp2.2\publish "BUILD"\GrammarProcessor\
xcopy src\SequenceHandler\SequenceHandler\bin\Release\netcoreapp2.2\publish  "BUILD"\SequenceHandler\
xcopy src\TableMGenerator\TableMGenerator\bin\Release\netcoreapp2.2\publish  "BUILD"\TableMGenerator\
xcopy src\SequenceStatistic\SequenceStatistic\bin\Release\netcoreapp2.2\publish  "BUILD"\SequenceStatistic\
xcopy /s src\Frontend\Frontend\wwwroot "BUILD"\Frontend\wwwroot\

xcopy config\application.properties "BUILD"\Frontend\
xcopy config\application.properties "BUILD"\Backend\
xcopy config\application.properties "BUILD"\GrammarConverter\
xcopy config\application.properties "BUILD"\GrammarProcessor\
xcopy config\application.properties  "BUILD"\SequenceHandler\
xcopy config\application.properties  "BUILD"\TableMGenerator\
xcopy config\application.properties  "BUILD"\SequenceStatistic\
xcopy config  "BUILD"\config\
xcopy run.cmd "BUILD"
xcopy stop.cmd "BUILD"

echo BUILD SUCCESS
exit /b 0

:BuildError
    echo Failed to build project.
    exit /b 1	
	