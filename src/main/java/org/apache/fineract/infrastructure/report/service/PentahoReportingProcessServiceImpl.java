/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.infrastructure.report.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.api.ReportingProcessService;
import org.apache.fineract.api.exception.ApiDataIntegrityException;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PentahoReportingProcessServiceImpl implements ReportingProcessService {

    private static final Logger logger = LoggerFactory.getLogger(PentahoReportingProcessServiceImpl.class);
    public static final String MIFOS_BASE_DIR = System.getProperty("user.home") + File.separator + ".mifosx";

    private boolean noPentaho = false;

    public PentahoReportingProcessServiceImpl() {
        // kick off pentaho reports server
        ClassicEngineBoot.getInstance().start();
        this.noPentaho = false;
    }

    @Override
    public void process(final String reportName, final Map<String, String> parameters, final OutputStream baos) {
        final Map<String, String> reportParams = parameters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(PARAMETER_PREFIX))
            .collect(Collectors.toMap(e -> e.getKey().substring(2), Map.Entry::getValue));

        final String outputTypeParam = reportParams.get(PARAM_OUTPUT_TYPE);
        final Locale locale = new Locale(reportParams.get(PARAM_LOCALE));

        String outputType = ReportType.HTML.name();
        if (StringUtils.isNotBlank(outputTypeParam)) {
            outputType = outputTypeParam;
        }

        if ((!outputType.equalsIgnoreCase(ReportType.HTML.name()) && !outputType.equalsIgnoreCase(ReportType.PDF.name()) && !outputType.equalsIgnoreCase(ReportType.XLS.name()) && !outputType.equalsIgnoreCase(ReportType.XLSX.name()) && !outputType.equalsIgnoreCase(ReportType.CSV.name()))) {
            throw new ApiDataIntegrityException("error.msg.invalid.outputType", "No matching Output Type: " + outputType);
        }

        if (this.noPentaho) {
            throw new ApiDataIntegrityException("error.msg.no.pentaho", "Pentaho is not enabled", "Pentaho is not enabled");
        }

        final String reportPath = MIFOS_BASE_DIR + File.separator + "pentahoReports" + File.separator + reportName + ".prpt";

        String outPutInfo = "Report path: " + reportPath;
        logger.info("Report path: {}", outPutInfo);

        // load report definition
        final ResourceManager manager = new ResourceManager();
        manager.registerDefaults();
        Resource res;

        try {
            res = manager.createDirectly(reportPath, MasterReport.class);
            final MasterReport masterReport = (MasterReport) res.getResource();
            final DefaultReportEnvironment reportEnvironment = (DefaultReportEnvironment) masterReport.getReportEnvironment();
            if (locale != null) {
                reportEnvironment.setLocale(locale);
            }
            addParametersToReport(masterReport, reportParams);

            if (ReportType.PDF.name().equalsIgnoreCase(outputType)) {
                PdfReportUtil.createPDF(masterReport, baos);
                // return Response.ok().entity(baos.toByteArray()).type("application/pdf").build();
            }

            if (ReportType.XLS.name().equalsIgnoreCase(outputType)) {
                ExcelReportUtil.createXLS(masterReport, baos);
            }

            if (ReportType.XLSX.name().equalsIgnoreCase(outputType)) {
                ExcelReportUtil.createXLSX(masterReport, baos);
            }

            if (ReportType.CSV.name().equalsIgnoreCase(outputType)) {
                CSVReportUtil.createCSV(masterReport, baos, "UTF-8");
            }

            if (ReportType.HTML.name().equalsIgnoreCase(outputType)) {
                HtmlReportUtil.createStreamHTML(masterReport, baos);
            }
        } catch (final ResourceException e) {
            // throw new ApiDataIntegrityException("error.msg.reporting.error", e.getMessage());
            logger.error("error.msg.reporting.error", e);
        } catch (final ReportProcessingException e) {
            // throw new ApiDataIntegrityException("error.msg.reporting.error", e.getMessage());
            logger.error("error.msg.reporting.error", e);
        } catch (final IOException e) {
            // throw new ApiDataIntegrityException("error.msg.reporting.error", e.getMessage());
            logger.error("error.msg.reporting.error", e);
        }

        throw new ApiDataIntegrityException("error.msg.invalid.outputType", "No matching Output Type: " + outputType);
    }

    @Override
    public String getType() {
        return "Pentaho";
    }

    private void addParametersToReport(final MasterReport report, final Map<String, String> queryParams) {

        // final AppUser currentUser = this.context.authenticatedUser();

        try {

            final ReportParameterValues rptParamValues = report.getParameterValues();
            final ReportParameterDefinition paramsDefinition = report.getParameterDefinition();

            /*
             * only allow integer, long, date and string parameter types and assume all mandatory - could go more
             * detailed like Pawel did in Mifos later and could match incoming and pentaho parameters better...
             * currently assuming they come in ok... and if not an error
             */
            for (final ParameterDefinitionEntry paramDefEntry : paramsDefinition.getParameterDefinitions()) {
                final String paramName = paramDefEntry.getName();
                if ((!paramName.equals("tenantUrl") && (!paramName.equals("userhierarchy") && !paramName.equals("username") && (!paramName.equals("password") && !paramName.equals("userid"))))) {

                    String outPutInfo2 = "paramName:" + paramName;
                    logger.info("paramName: {}", outPutInfo2);

                    final String pValue = queryParams.get(paramName);
                    if (StringUtils.isBlank(pValue)) {
                        throw new ApiDataIntegrityException("error.msg.reporting.error",
                                "Pentaho Parameter: " + paramName + " - not Provided");
                    }

                    final Class<?> clazz = paramDefEntry.getValueType();

                    String outPutInfo3 = "addParametersToReport(" + paramName + " : " + pValue + " : " + clazz.getCanonicalName() + ")";
                    logger.info("outputInfo: {}", outPutInfo3);

                    if (clazz.getCanonicalName().equalsIgnoreCase("java.lang.Integer")) {
                        rptParamValues.put(paramName, Integer.parseInt(pValue));
                    } else if (clazz.getCanonicalName().equalsIgnoreCase("java.lang.Long")) {
                        rptParamValues.put(paramName, Long.parseLong(pValue));
                    } else if (clazz.getCanonicalName().equalsIgnoreCase("java.sql.Date")) {
                        rptParamValues.put(paramName, Date.valueOf(pValue));
                    } else {
                        rptParamValues.put(paramName, pValue);
                    }
                }

            }

            // tenant database name and current user's office hierarchy
            // passed as parameters to allow multitenant penaho reporting
            // and
            // data scoping
            // TODO: @vidakovic still have to remove all dependencies on these internal Fineract implementation details; these parameters will be set as parameters before calling this function
            /*
            final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            final FineractPlatformTenantConnection tenantConnection = tenant.getConnection();
            String tenantUrl = driverConfig.constructProtocol(tenantConnection.getSchemaServer(), tenantConnection.getSchemaServerPort(),
                    tenantConnection.getSchemaName());
            final String userhierarchy = currentUser.getOffice().getHierarchy();
            logger.info("db URL: {}      userhierarchy:{}", tenantUrl, userhierarchy);

            rptParamValues.put("userhierarchy", userhierarchy);

            final Long userid = currentUser.getId();
            logger.info("db URL: {}      userid:{}", tenantUrl, userid);

            rptParamValues.put("userid", userid);

            rptParamValues.put("tenantUrl", tenantUrl);
            rptParamValues.put("username", tenantConnection.getSchemaUsername());
            rptParamValues.put("password", tenantConnection.getSchemaPassword());
             */
        } catch (final Exception e) {
            // logger.error("error.msg.reporting.error:" + e.getMessage());
            logger.error("error.msg.reporting.error:", e);
            // throw new PlatformDataIntegrityException("error.msg.reporting.error", e.getMessage());
        }
    }
}
