@echo off
set "JAVA_BIN=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot\bin"
if exist "%JAVA_BIN%\java.exe" (
    set "PATH=%JAVA_BIN%;%PATH%"
)
echo Compiling source files...
javac -encoding UTF-8 -cp ".;mysql-connector-j-8.3.0.jar" *.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)
echo Starting RapidRack Warehouse Management System...
java -cp ".;mysql-connector-j-8.3.0.jar" WarehouseApp
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application closed with an error.
    pause
)
