package org.openmrs.module.pharmacyreports.web.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.pharmacyreports.api.reporting.provider.MonthlyReportProvider;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * controller for pharmacyReport
 */
@Controller
public class PharmacyMonthlyReportsController {

	private final Log log = LogFactory.getLog(getClass());

	private static final String FORM_VIEW = "module/pharmacyreports/pharmacyMonthlyReportForm";

	/**
	 * monthly report
	 */
	@RequestMapping(method = RequestMethod.POST, value = "module/pharmacyreports/pharmacyReport.form", params = "monthlyReport")
	public String pharmacyMonthlyReport(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response ) throws Exception {

		if (StringUtils.isEmpty(request.getParameter("evaluationDate"))){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "You did not set start date");
			return "redirect:pharmacyReport.form";
		}

		String effectiveDate = request.getParameter("evaluationDate");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = sdf.parse(effectiveDate);
		Date endDate = new Date();

		MonthlyReportProvider queuedReport = new MonthlyReportProvider();

		try{
			CohortDefinition cohortDefinition = queuedReport.getCohortDefinition();
			cohortDefinition.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));

			ReportDefinition reportDefinition = queuedReport.getReportDefinition();
			EvaluationContext evaluationContext = new EvaluationContext();
			evaluationContext.setEvaluationDate(endDate);
			evaluationContext.addParameterValue(ReportingConstants.START_DATE_PARAMETER.getName(), startDate);
			//evaluationContext.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), endDate);


			// get the cohort
			CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
			Cohort cohort = cohortDefinitionService.evaluate(cohortDefinition, evaluationContext);
			evaluationContext.setBaseCohort(cohort);

			ReportData reportData = Context.getService(ReportDefinitionService.class)
					.evaluate(reportDefinition, evaluationContext);

			File xlsFile = File.createTempFile("patient_mgt_rpt", ".xls");
			OutputStream stream = new BufferedOutputStream(new FileOutputStream(xlsFile));

			final ReportDesign design = queuedReport.getReportDesign();
			ExcelTemplateRenderer renderer = new ExcelTemplateRenderer() {
				public ReportDesign getDesign(String argument) {
					return design;
				}
			};
			renderer.render(reportData, "reportManagement", stream);
			stream.close();

			response.setHeader("Content-disposition", "attachment; filename=" + "patientMgtReport" + ".xls");
			response.setContentType("application/vnd.ms-excel");
			OutputStream excelFileDownload = response.getOutputStream();
			FileInputStream fileInputStream = new FileInputStream(xlsFile);

			IOUtils.copy(fileInputStream, excelFileDownload);
			fileInputStream.close();
			excelFileDownload.flush();
			excelFileDownload.close();
			xlsFile.delete();
		}  catch (Exception e){
			e.getMessage();
			//e.printStackTrace();
		}
		return "redirect:pharmacyReport.form";
	}

    /**
     *
     * methods for GET
     */
    @RequestMapping(method = RequestMethod.GET, value = "module/pharmacyreports/pharmacyReport.form")
    public String requestMonthlyReport() {
        return FORM_VIEW;
    }
}
