@echo on

rem Copy Apache Fineract JAR
md build\run
copy .\fineract\fineract-provider\build\libs\fineract-provider.jar build\run

rem Add our fineract-pentaho reporting plugin, like so:

powershell -command "Expand-Archive -Force 'build\distributions\fineract-pentaho.zip' 'build\run'"
del build\run\fineract-pentaho.jar

md %USERPROFILE%\.mifosx\pentahoReports\
copy build\run\pentahoReports\* %USERPROFILE%\.mifosx\pentahoReports\
rmdir /Q /S build\run\pentahoReports

rem Start!

java -Dloader.path=build\run\lib\ -jar build\run\fineract-provider.jar