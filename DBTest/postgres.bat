@echo off
REM Set the classpath
SET CLASSPATH=dbUtil-0.0.3-SNAPSHOT.jar;postgresql-42.7.2.jar

REM Run the Java application
java -cp "%CLASSPATH%" app.trigyn.utility.dbUtil.SchemaCompare

REM Pause to keep the command prompt window open after execution
pause
