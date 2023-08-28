# Pentaho Reporting Plugin for Apache Fineract

This is a [_Plugin_ for Apache Fineract](https://github.com/apache/fineract/blob/maintenance/1.6/fineract-doc/src/docs/en/deployment.adoc). The original work is this one https://github.com/vorburger/fineract-pentaho.

See [TODO](TODO.md) for possible future follow-up enhancement work.

The Pentaho reports has been updated to the version 9.5, please use the [`Pentaho Report Designed version 9.5`](https://mifos.jfrog.io/artifactory/libs-snapshot-local/org/pentaho/reporting/prd-ce/9.5.0.0-SNAPSHOT/prd-ce-9.5.0.0-20230108.081758-1.zip) 

## For Developers

Add the repository https://mifos.jfrog.io/artifactory/libs-snapshot-local/

Maven
```bash
    <dependency>
        <groupId>community.mifos</groupId>
        <artifactId>pentaho-plugin</artifactId>
        <version>1.9.0-SNAPSHOT</version>
    </dependency>
```
Gradle
```bash
    compile(group: 'community.mifos', name: 'pentaho-plugin', version: '1.9.0-SNAPSHOT')
```

## Download, Unzip and Use the binaries 

```bash
    mkdir fineract-pentaho  && cd fineract-pentaho
    wget https://mifos.jfrog.io/artifactory/libs-snapshot-local/community/mifos/pentaho-plugin/1.9.0-SNAPSHOT/pentaho-plugin-1.9.0-20230827.055940-1.jar 
```

## Build & Use For Linux Users

This project is currently only tested against the very latest and greatest
bleeding edge Fineract `develop` branch on Linux Ubuntu 20.04LTS. Building and using it against
other versions may be possible, but is not tested or documented here.

1. Download and compile

```bash
    git clone https://github.com/openMF/fineract-pentaho.git
    cd fineract-pentaho && ./mvnw -Dmaven.test.skip=true clean package && cd ..
```
2. Export the Location of Pentaho Reports (prpt files) in a variable required by the Plugin

```bash
    export FINERACT_PENTAHO_REPORTS_PATH="$PWD/fineract-pentaho/pentahoReports/"
```    

3. Execute Apache Fineract with the location of the Mifos Pentaho Plugin library for Apache Fineract

```bash
java -Dloader.path=$MIFOS_PENTAHO_PLUGIN_HOME/libs/ -jar $APACHE_FINERACT_HOME/fineract-provider.jar
```

4. Test the Pentaho Reports Execution using the following curl example or through the Mifos Web App in the Reports Menu

```bash
    curl --location --request GET 'https://localhost:8443/fineract-provider/api/v1/runreports/Expected%20Payments%20By%20Date%20-%20Formatted?tenantIdentifier=default&locale=en&dateFormat=dd%20MMMM%20yyyy&R_startDate=01%20January%202022&R_endDate=02%20January%202023&R_officeId=1&output-type=PDF&R_loanOfficerId=-1' \
--header 'Fineract-Platform-TenantId: default' \
--header 'Authorization: Basic bWlmb3M6cGFzc3dvcmQ='
```

5. The output must be a PDF with the Expected Payment By Date Formated information in it (maybe it could have blank or zeroes if it is a fresh Fineract Setup)

![alt text](https://github.com/openMF/fineract-pentaho/blob/1.8/img/screenshot_pentaho_report.png?raw=true)

The API call (above) should not fail if you follow the steps as shown, and all conditions met for the version of Apache Fineract

If the API call (above) [fails with](https://issues.apache.org/jira/browse/FINERACT-1173) 
_`"There is no ReportingProcessService registered in the ReportingProcessServiceProvider for this report type: Pentaho"`_, 
then this Fineract Pentaho Plugin has not been correctly registered & loaded by Apache Fineract.

## What script does

The script basically just creates the following directory structure and download the required files by the Pentaho Report Engine and the Database Connection (In this case MySQL):

```bash
    fineract-provider.jar
    lib/
        antlr-complete-3.5.2.jar
asm-3.2.jar
avalon-framework-4.1.5.jar
barbecue-1.5-beta1.jar
barcode4j-2.0.jar
batik-anim-1.9.1.jar
batik-awt-util-1.9.1.jar
batik-bridge-1.9.1.jar
batik-codec-1.9.1.jar
batik-constants-1.9.1.jar
batik-css-1.9.1.jar
batik-dom-1.9.1.jar
batik-ext-1.9.1.jar
batik-gui-util-1.9.1.jar
batik-gvt-1.9.1.jar
batik-i18n-1.9.1.jar
batik-parser-1.9.1.jar
batik-script-1.9.1.jar
batik-svg-dom-1.9.1.jar
batik-transcoder-1.9.1.jar
batik-util-1.9.1.jar
batik-xml-1.9.1.jar
bcmail-jdk14-138.jar
bsf-2.4.0.jar
bsh-1.3.0.jar
classic-core-9.5.0.0-SNAPSHOT.jar
classic-extensions-9.5.0.0-SNAPSHOT.jar
classic-extensions-scripting-9.5.0.0-SNAPSHOT.jar
commons-codec-1.15.jar
commons-collections4-4.4.jar
commons-compress-1.20.jar
commons-database-model-9.5.0.0-SNAPSHOT.jar
commons-dbcp-1.4.jar
commons-math3-3.6.1.jar
commons-pool-1.6.jar
commons-vfs2-2.7.0.jar
curvesapi-1.06.jar
ehcache-core-2.5.1.jar
flute-9.5.0.0-SNAPSHOT.jar
graal-sdk-22.2.0.jar
groovy-all-2.4.8.jar
guava-17.0.jar
httpclient-4.5.13.jar
httpcore-4.4.15.jar
icu4j-71.1.jar
itext-2.1.7.jar
itext-rtf-2.1.7.jar
jakarta.activation-1.2.2.jar
jakarta.xml.bind-api-2.3.3.jar
javax.mail-1.6.1.jar
js-1.7R3.jar
js-22.2.0.jar
js-scriptengine-22.2.0.jar
libbase-9.5.0.0-SNAPSHOT.jar
libdocbundle-9.5.0.0-SNAPSHOT.jar
libfonts-9.5.0.0-SNAPSHOT.jar
libformat-9.5.0.0-SNAPSHOT.jar
libformula-9.5.0.0-SNAPSHOT.jar
libloader-9.5.0.0-SNAPSHOT.jar
libpixie-9.5.0.0-SNAPSHOT.jar
librepository-9.5.0.0-SNAPSHOT.jar
libserializer-9.5.0.0-SNAPSHOT.jar
libsparkline-9.5.0.0-SNAPSHOT.jar
libswing-9.5.0.0-SNAPSHOT.jar
libxml-9.5.0.0-SNAPSHOT.jar
pentaho-plugin-1.9.0-20230827.055940-1.jar
regex-22.2.0.jar
rsyntaxtextarea-1.3.2.jar
sac-1.3.jar
truffle-api-22.2.0.jar
wizard-core-9.5.0.0-SNAPSHOT.jar
xml-apis-ext-1.3.04.jar
xmlgraphics-commons-2.2.jar
```

and then it launches Apache Fineract with the Pentaho Plugin and all its dependencies. 

Please note that the library will work using the latest Apache Fineract development branch (27th August 2023), the commons-logging and slfj have been removed from the java classpath, also make sure you got installed the type fonts required by the reports. This Pentaho plugin will work only on Tomcat 10+. 

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
