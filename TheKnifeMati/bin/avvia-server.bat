@echo off
setlocal

cd /d "%~dp0"

set "JAVA_EXE="

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

if not defined JAVA_EXE if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe"
)

if not defined JAVA_EXE (
    where java >nul 2>nul
    if errorlevel 1 (
        echo Errore: Java non trovato nel PATH.
        echo Installa Java 22+ e riprova.
        pause
        exit /b 1
    )
    set "JAVA_EXE=java"
)

if not exist "TheKnifeServer.jar" (
    echo Errore: file non trovato: TheKnifeServer.jar
    pause
    exit /b 1
)

"%JAVA_EXE%" -jar "TheKnifeServer.jar"
if errorlevel 1 (
    echo.
    echo Avvio fallito. Se vedi UnsupportedClassVersionError, usa Java 22 o superiore.
    echo Java usato: %JAVA_EXE%
    echo Impossibile avviare TheKnifeServer.
    pause
    exit /b 1
)

exit /b 0
