@echo off
cd /d "%~dp0.."
mvn exec:java -Dexec.mainClass="com.example.theknife.server.ServerMain" -Dexec.args="%*"