# Pentaho Reporting Plugin for Apache Fineract

see https://issues.apache.org/jira/browse/FINERACT-1127 for background.

see [TODO](TODO.md) for possible future follow-up enhancement work.


## Usage

    git clone https://github.com/apache/fineract.git
    cd fineract && ./gradlew bootJar && cd ..

    git clone https://github.com/vorburger/fineract-pentaho.git
    cd fineract-pentaho && ./gradlew jar && cd ..

    java -cp fineract-pentaho/build/libs/fineract-pentaho.jar:$(ls fineract/build/libs/fineract-provider*.jar) org.springframework.boot.loader.JarLauncher


## TODO

1. fix NoClassDefFoundError

1. test it (functionally; does this actually work?!)

1. add minimal documentation above (e.g. `java -jar f.jar;fineract-pentaho.jar` ?)
