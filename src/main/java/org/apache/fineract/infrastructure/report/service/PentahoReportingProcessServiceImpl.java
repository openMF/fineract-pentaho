/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.infrastructure.report.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import static org.apache.fineract.infrastructure.core.domain.FineractPlatformTenantConnection.toJdbcUrl;
import static org.apache.fineract.infrastructure.core.domain.FineractPlatformTenantConnection.toProtocol;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.report.annotation.ReportService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@ReportService(type = "Pentaho")
public class PentahoReportingProcessServiceImpl implements ReportingProcessService {

    private static final Logger logger = LoggerFactory.getLogger(PentahoReportingProcessServiceImpl.class);
    public static final String MIFOS_BASE_DIR = System.getProperty("user.home") + File.separator + ".mifosx";

    private final PlatformSecurityContext context;
    private final DataSource tenantDataSource;

    @Autowired
    public PentahoReportingProcessServiceImpl(final PlatformSecurityContext context,
final @Qualifier("hikariTenantDataSource") DataSource tenantDataSource) {
        ClassicEngineBoot.getInstance().start();
        this.tenantDataSource = tenantDataSource;
        this.context = context;
    }

    @Override
    public Response processRequest(final String reportName, final MultivaluedMap<String, String> queryParams) {
        final var outputTypeParam = queryParams.getFirst("output-type");
        final var reportParams = getReportParams(queryParams);
        final var locale = ApiParameterHelper.extractLocale(queryParams);

        var outputType = "HTML";
        if (StringUtils.isNotBlank(outputTypeParam)) {
            outputType = outputTypeParam;
        }

        if ((!outputType.equalsIgnoreCase("HTML") && !outputType.equalsIgnoreCase("PDF") && !outputType.equalsIgnoreCase("XLS") && !outputType.equalsIgnoreCase("XLSX") && !outputType.equalsIgnoreCase("CSV"))) {
            throw new PlatformDataIntegrityException("error.msg.invalid.outputType", "No matching Output Type: " + outputType);
        }

        final var reportPath = MIFOS_BASE_DIR + File.separator + "pentahoReports" + File.separator + reportName + ".prpt";
        var outPutInfo = "Report path: " + reportPath;
        logger.info("Report path: {}", outPutInfo);

        // load report definition
        final var manager = new ResourceManager();
        manager.registerDefaults();
        Resource res;

        try {
            res = manager.createDirectly(reportPath, MasterReport.class);
            final var masterReport = (MasterReport) res.getResource();
            final var reportEnvironment = (DefaultReportEnvironment) masterReport.getReportEnvironment();
            if (locale != null) {
                reportEnvironment.setLocale(locale);
            }
            addParametersToReport(masterReport, reportParams);

            final var baos = new ByteArrayOutputStream();

            if ("PDF".equalsIgnoreCase(outputType)) {
                PdfReportUtil.createPDF(masterReport, baos);
                return Response.ok().entity(baos.toByteArray()).type("application/pdf").build();

            } else if ("XLS".equalsIgnoreCase(outputType)) {
                ExcelReportUtil.createXLS(masterReport, baos);
                return Response.ok().entity(baos.toByteArray()).type("application/vnd.ms-excel")
                        .header("Content-Disposition", "attachment;filename=" + reportName.replaceAll(" ", "") + ".xls").build();

            } else if ("XLSX".equalsIgnoreCase(outputType)) {
                ExcelReportUtil.createXLSX(masterReport, baos);
                return Response.ok().entity(baos.toByteArray()).type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .header("Content-Disposition", "attachment;filename=" + reportName.replaceAll(" ", "") + ".xlsx").build();

            } else if ("CSV".equalsIgnoreCase(outputType)) {
                CSVReportUtil.createCSV(masterReport, baos, "UTF-8");
                return Response.ok().entity(baos.toByteArray()).type("text/csv")
                        .header("Content-Disposition", "attachment;filename=" + reportName.replaceAll(" ", "") + ".csv").build();

            } else if ("HTML".equalsIgnoreCase(outputType)) {
                HtmlReportUtil.createStreamHTML(masterReport, baos);
                return Response.ok().entity(baos.toByteArray()).type("text/html").build();

            } else {
                throw new PlatformDataIntegrityException("error.msg.invalid.outputType", "No matching Output Type: " + outputType);

            }
        } catch (final ResourceException | ReportProcessingException | IOException e) {
            logger.error("Pentaho failed", e);
            throw new PlatformDataIntegrityException("error.msg.reporting.error", "Pentaho failed: " + e.getMessage());
        }
    }

    private void addParametersToReport(final MasterReport report, final Map<String, String> queryParams) {
        final var currentUser = this.context.authenticatedUser();
        try {
            final var rptParamValues = report.getParameterValues();
            final var paramsDefinition = report.getParameterDefinition();

            // only allow integer, long, date and string parameter types and assume all mandatory - could go more
            // detailed like Pawel did in Mifos later and could match incoming and Pentaho parameters better...
            // currently assuming they come in ok... and if not an error
            for (final ParameterDefinitionEntry paramDefEntry : paramsDefinition.getParameterDefinitions()) {
                final var paramName = paramDefEntry.getName();
                if ((!paramName.equals("tenantUrl") && (!paramName.equals("userhierarchy") && !paramName.equals("username") && (!paramName.equals("password") && !paramName.equals("userid"))))) {

                    var outPutInfo2 = "paramName:" + paramName;
                    logger.info("paramName: {}", outPutInfo2);

                    final var pValue = queryParams.get(paramName);
                    if (StringUtils.isBlank(pValue)) {
                        throw new PlatformDataIntegrityException("error.msg.reporting.error",
                                "Pentaho Parameter: " + paramName + " - not Provided");
                    }

                    final Class<?> clazz = paramDefEntry.getValueType();
                    var outPutInfo3 = "addParametersToReport(" + paramName + " : " + pValue + " : " + clazz.getCanonicalName() + ")";
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

            // Tenant database name and current user's office hierarchy
            // passed as parameters to allow multitenant Pentaho reporting
            // and data scoping
            final var tenant = ThreadLocalContextUtil.getTenant();
            final var tenantConnection = tenant.getConnection();
            String protocol = toProtocol(this.tenantDataSource);
            var tenantUrl = toJdbcUrl(protocol ,tenantConnection.getSchemaServer(), tenantConnection.getSchemaServerPort(),
                    tenantConnection.getSchemaName(), tenantConnection.getSchemaConnectionParameters());
            final var userhierarchy = currentUser.getOffice().getHierarchy();
            var outPutInfo4 = "db URL:" + tenantUrl + "      userhierarchy:" + userhierarchy;
            logger.info(outPutInfo4);

            rptParamValues.put("userhierarchy", userhierarchy);

            final var userid = currentUser.getId();
            var outPutInfo5 = "db URL:" + tenantUrl + "      userid:" + userid;
            logger.info(outPutInfo5);

            rptParamValues.put("userid", userid);

            rptParamValues.put("tenantUrl", tenantUrl);
            rptParamValues.put("username", tenantConnection.getSchemaUsername());
            rptParamValues.put("password", tenantConnection.getSchemaPassword());
        } catch (final Exception e) {
            logger.error("error.msg.reporting.error:", e);
            throw new PlatformDataIntegrityException("error.msg.reporting.error", e.getMessage());
        }
    }

    @Override
    public Map<String, String> getReportParams(final MultivaluedMap<String, String> queryParams) {
        final Map<String, String> reportParams = new HashMap<>();
        final var keys = queryParams.keySet();
        String pKey;
        String pValue;
        for (final String k : keys) {
            if (k.startsWith("R_")) {
                pKey = k.substring(2);
                pValue = queryParams.get(k).get(0);
                reportParams.put(pKey, pValue);
            }
        }
        return reportParams;
    }
}
