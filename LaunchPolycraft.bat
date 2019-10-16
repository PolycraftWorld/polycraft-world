@echo off
setlocal enableDelayedExpansion

:launchLoop
REM run Minecraft:
call gradlew setupDecompWorkspace
call gradlew build
call gradlew runClient
if "!-replaceable!"=="true" (
    goto :launchLoop
)
