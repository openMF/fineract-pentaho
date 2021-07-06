# Pentaho Reporting Plugin for Apache Fineract

see https://github.com/vorburger/fineract-pentaho for source code.

This is a [_Plugin_ for Apache Fineract](https://github.com/apache/fineract/blob/develop/fineract-doc/src/docs/en/deployment.adoc). 
(This code lives here instead of being part of the core Apache Fineract repository because of [software licenses](#license).)

see [TODO](TODO.md) for possible future follow-up enhancement work.


## Build & Use For Linux Users

This project is currently only tested against the very latest and greatest
bleeding edge Fineract `develop` branch.  Building and using it against
older versions may be possible, but is not tested or documented here.

    git clone https://github.com/apache/fineract.git
    cd fineract && ./gradlew bootJar && cd ..

    git clone https://github.com/vorburger/fineract-pentaho.git
    cd fineract-pentaho && ./gradlew -x test distZip && cd ..

    mkdir -p ~/.mifosx/pentahoReports/
    cp ./fineract-pentaho/pentahoReports/* ~/.mifosx/pentahoReports/

    ./fineract-pentaho/run

    curl --insecure --location --request GET 'https://localhost:8443/fineract-provider/api/v1/runreports/Expected%20Payments%20By%20Date%20-%20Formatted?R_endDate=2013-04-30&R_loanOfficerId=-1&R_officeId=1&R_startDate=2013-04-16&output-type=PDF&R_officeId=1' --header 'Fineract-Platform-TenantId: default' --header 'Authorization: Basic bWlmb3M6cGFzc3dvcmQ='

The API call (above) should not fail if you follow the steps as shown, and all conditions met for the version of fineract

## Build & Use For Windows Users

This project is currently only tested against the very latest and greatest bleeding edge Fineract `develop` branch. It works like a charm after ([PR #1671](https://github.com/apache/fineract/pull/1671)). Building and using it against older versions may not work. You might want to cherry pick the changes made in the above PR

    git clone https://github.com/apache/fineract.git
    cd fineract && ./gradlew bootJar && cd ..

    git clone https://github.com/vorburger/fineract-pentaho.git
    cd fineract-pentaho && ./gradlew -x test distZip && cd ..

    mkdir -p ~/.mifosx/pentahoReports/
    cp ./fineract-pentaho/pentahoReports/* ~/.mifosx/pentahoReports/

    ./fineract-pentaho/run.bat

To test this you can any REST Client tool (like Postman) with the following parameters

Method : GET
URL : https://localhost:8443/fineract-provider/api/v1/runreports/Expected%20Payments%20By%20Date%20-%20Formatted?R_endDate=2013-04-30&R_loanOfficerId=-1&R_officeId=1&R_startDate=2013-04-16&output-type=PDF&R_officeId=1

**HEADERS**
Fineract-Platform-TenantId: default
Content-Type: application/json

Authorization: Basic bWlmb3M6cGFzc3dvcmQ=    i.e. (username = mifos & password = password)


If the API call (above) [fails with](https://issues.apache.org/jira/browse/FINERACT-1173) 
_`"There is no ReportingProcessService registered in the ReportingProcessServiceProvider for this report type: Pentaho"`_, 
then this Fineract Pentaho Plugin has not been correctly registered & loaded by Apache Fineract.



## What both scripts do

Both scripts (windows and Linux alike) basically just creates the following directory structure:

    fineract-provider.jar
    lib/fineract-pentaho.jar
    lib/pentaho-reporting-*.jar
    lib/lib*.jar

and then launches Apache Fineract with the Pentaho Plugin and all its JARs like this:

    java -Dloader.path=lib/ -jar fineract-provider.jar

See also [`PentahoReportsTest`](src/test/java/org/mifos/fineract/pentaho/PentahoReportsTest.java) and the [`test`](test) script.


## License

This code used to be part of the Mifos codebase before it became Apache Fineract.
During that move, the Pentaho related code had to be removed, because Pentaho's license
prevents code using it from being part of an Apache Software Foundation hosted project.

The correct technical solution to resolve such conundrums is to use a plugin architecture - which is what this is.

Note that the code and report templates in this git repo itself are
[licensed to you under the Mozilla Public License 2.0 (MPL)](https://github.com/openMF/fineract-pentaho/blob/develop/LICENSE).
This is a separate question than the license that Pentaho itself (i.e. the JAR/s of Pentaho) are made available under.


## Contribute

If this Fineract plugin project is useful to you, please contribute back to it (and
Fineract) by raising Pull Requests yourself with any enhancements you make, and by helping
to maintain this project by helping other users on Issues and reviewing PR from others
(you will be promoted to committer on this project when you contribute).  We recommend
that you _Watch_ and _Star_ this project on GitHub to make it easy to get notified.
