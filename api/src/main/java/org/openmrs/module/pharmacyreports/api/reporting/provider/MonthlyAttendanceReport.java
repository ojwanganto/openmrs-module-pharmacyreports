package org.openmrs.module.pharmacyreports.api.reporting.provider;

import org.apache.commons.io.IOUtils;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Definition for Monthly Attendance Report
 */
public class MonthlyAttendanceReport {
    MonthlyReportsIndicatorLibrary baseIndicator = new MonthlyReportsIndicatorLibrary();

	public ReportDefinition getReportDefinition() {

        ReportDefinition report = new PeriodIndicatorReportDefinition();
        report.setName("Monthly Attendance Report");

        // set up parameters
        Parameter facility = new Parameter();
        facility.setName("locationList");
        facility.setType(Location.class);

        report.addParameter(ReportingConstants.START_DATE_PARAMETER);
        report.addParameter(ReportingConstants.END_DATE_PARAMETER);
        report.addParameter(facility);

        Map<String, Object> periodMappings = new HashMap<String, Object>();
        periodMappings.put("startDate", "${startDate}");
        periodMappings.put("endDate", "${endDate}");
        periodMappings.put("locationList", "${locationList}");


        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();

        dsd.addParameter(ReportingConstants.START_DATE_PARAMETER);
        dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);
        dsd.addParameter(facility);

        dsd.addColumn("B-01", "Males Below 15", new Mapped<CohortIndicator>(baseIndicator.malePatientsTested(), periodMappings), "");
        dsd.addColumn("B-02", "Males 15 or more", new Mapped<CohortIndicator>(baseIndicator.malePatientsWhoReportedDisclosure(), periodMappings), "");
        dsd.addColumn("B-03", "Females Below 15", new Mapped<CohortIndicator>(baseIndicator.malePatientsWhoReceivedCondoms(), periodMappings), "");
        dsd.addColumn("B-04", "Females 15 or more", new Mapped<CohortIndicator>(baseIndicator.malePatientsProvidedWithModernContraceptives(), periodMappings), "");
        dsd.addColumn("B-05", "Males Below 15", new Mapped<CohortIndicator>(baseIndicator.malePatientsScreenedForSTI(), periodMappings), "");
        dsd.addColumn("B-06", "Males 15 or more", new Mapped<CohortIndicator>(baseIndicator.malePatientsOnPWPPackage(), periodMappings), "");

        dsd.addColumn("C-01", "Males Below 15", new Mapped<CohortIndicator>(baseIndicator.femalePatientsTested(), periodMappings), "");
        dsd.addColumn("C-02", "Males 15 or more", new Mapped<CohortIndicator>(baseIndicator.femalePatientsWhoReportedDisclosure(), periodMappings), "");
        dsd.addColumn("C-03", "Females Below 15", new Mapped<CohortIndicator>(baseIndicator.femalePatientsWhoReceivedCondoms(), periodMappings), "");
        dsd.addColumn("C-04", "Females 15 or more", new Mapped<CohortIndicator>(baseIndicator.femalePatientsProvidedWithModernContraceptives(), periodMappings), "");
        dsd.addColumn("C-05", "Males Below 15", new Mapped<CohortIndicator>(baseIndicator.femalePatientsScreenedForSTI(), periodMappings), "");
        dsd.addColumn("C-06", "Males 15 or more", new Mapped<CohortIndicator>(baseIndicator.femalePatientsOnPWPPackage(), periodMappings), "");


        report.addDataSetDefinition(dsd, periodMappings);

		return report;
	}

	public ReportDesign getReportDesign() {
		ReportDesign design = new ReportDesign();
		design.setName("Monthly Attendance Report");
		design.setReportDefinition(this.getReportDefinition());
		design.setRendererType(ExcelTemplateRenderer.class);

		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("templates/pharmacyMonthlyReport.xls");

		if (is == null)
			throw new APIException("Could not find report template.");

		try {
			resource.setContents(IOUtils.toByteArray(is));
		} catch (IOException ex) {
			throw new APIException("Could not create report design for Monthly Attendance Report.", ex);
		}

		IOUtils.closeQuietly(is);
		design.addResource(resource);

		return design;
	}

}