@echo off
setlocal

cd /d "%~dp0"

set "JAVA_EXE="
set "JAVAW_EXE="

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javaw.exe" (
    set "JAVAW_EXE=%JAVA_HOME%\bin\javaw.exe"
)

if not defined JAVA_EXE if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe"
)
if not defined JAVAW_EXE if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\javaw.exe" (
    set "JAVAW_EXE=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\javaw.exe"
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

if not defined JAVAW_EXE (
    where javaw >nul 2>nul
    if errorlevel 1 (
        set "JAVAW_EXE=%JAVA_EXE%"
    ) else (
        set "JAVAW_EXE=javaw"
    )
)

if not exist "TheKnifeClient.jar" (
    echo Errore: file non trovato: TheKnifeClient.jar
    pause
    exit /b 1
)

"%JAVAW_EXE%" -jar "TheKnifeClient.jar"
if errorlevel 1 (
    echo Avvio con javaw fallito, provo con java...
    "%JAVA_EXE%" -jar "TheKnifeClient.jar"
    if errorlevel 1 (
        echo.
        echo Avvio fallito. Se vedi UnsupportedClassVersionError, usa Java 22 o superiore.
        echo Java usato: %JAVA_EXE%
        echo Impossibile avviare TheKnifeClient.
        pause
        exit /b 1
    )
)

exit /b 0
