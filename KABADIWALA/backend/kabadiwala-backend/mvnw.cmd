@echo off
set "JAVA_HOME=C:\Users\kumar\tools\jdk-17.0.12+7"
set "PATH=%JAVA_HOME%\bin;C:\Users\kumar\tools\apache-maven-3.9.9\bin;%PATH%"
cmd /c mvn %*
