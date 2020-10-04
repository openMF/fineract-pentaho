_**THIS IS WIP AND DOES NOT WORK, YET...**_

# Pentaho Reporting Plugin for Apache Fineract

see https://issues.apache.org/jira/browse/FINERACT-1127 for background.

see [TODO](TODO.md) for possible future follow-up enhancement work.


## Usage

    git clone https://github.com/apache/fineract.git
    cd fineract && ./gradlew bootJar && cd ..

    git clone https://github.com/vorburger/fineract-pentaho.git
    cd fineract-pentaho && ./gradlew jar && cd ..

    mkdir -p ~/.mifosx/pentahoReports/
    cp ./fineract-pentaho/pentahoReports/* ~/.mifosx/pentahoReports/

    ./fineract-pentaho/run


## TODO

1. fix `java.lang.IllegalStateException: Booting the report-engine failed.` - try "fully flat" classpath?

1. test it (functionally; does this actually work?!)

1. add minimal documentation above (e.g. `java -jar f.jar;fineract-pentaho.jar` ?)

1. make hard-coded `~/.mifosx/pentahoReports` configurable?
