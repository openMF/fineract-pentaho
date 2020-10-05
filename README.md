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

    curl --insecure --location --request GET 'https://localhost:8443/fineract-provider/api/v1/runreports/Expected%20Payments%20By%20Date%20-%20Formatted?R_endDate=2013-04-30&R_loanOfficerId=-1&R_officeId=1&R_startDate=2013-04-16&output-type=PDF&R_officeId=1' --header 'Fineract-Platform-TenantId: default' --header 'Authorization: Basic bWlmb3M6cGFzc3dvcmQ='

The API call (above) will fail on the server (see log) due to an intern error (somehow
the SQL query in that particular report is currently actually broken), but this illustrates
that the integration of Pentaho as a Fineract Plugin basically works.
([FINERACT-1176](https://issues.apache.org/jira/browse/FINERACT-1176) tracks improving API response.)
