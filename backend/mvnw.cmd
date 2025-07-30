@echo off
setlocal
set MVNW_CMD=.mvn\wrapper\maven-wrapper.jar
if not exist %MVNW_CMD% (
  echo Maven wrapper JAR not found!
  exit /b 1
)
java -jar %MVNW_CMD% %*
