@echo off
rem Starts KWM without a console window. Needs Java 17 or newer on PATH.
where javaw >nul 2>nul || (
    echo KWM needs Java 17 or newer. Get it from https://adoptium.net
    pause
    exit /b 1
)
start "" javaw -cp "%~dp0lib\*" org.example.MainKt
