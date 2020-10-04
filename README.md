# Pentaho Reporting Plugin for Apache Fineract

see https://github.com/vorburger/fineract-pentaho for source code.

see https://issues.apache.org/jira/browse/FINERACT-1127 for background.

see [TODO](TODO.md) for possible future follow-up enhancement work.


## Build & Use

    git clone https://github.com/apache/fineract.git
    cd fineract && ./gradlew bootJar && cd ..

    git clone https://github.com/vorburger/fineract-pentaho.git
    cd fineract-pentaho && ./gradlew distZip && cd ..

    mkdir -p ~/.mifosx/pentahoReports/
    cp ./fineract-pentaho/pentahoReports/* ~/.mifosx/pentahoReports/

    ./fineract-pentaho/run
