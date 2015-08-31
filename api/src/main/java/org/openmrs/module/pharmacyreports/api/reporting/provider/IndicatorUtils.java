/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pharmacyreports.api.reporting.provider;

import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;

import java.util.Date;

/**
 * Library of common indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
public class IndicatorUtils {

	/**
	 * Utility method to create a new cohort indicator
	 * @param description the indicator description
	 * @return the cohort indicator
	 */
    public static CohortIndicator createCohortIndicator(String description, Mapped<CohortDefinition> mappedCohort) {
        CohortIndicator ind = new CohortIndicator(description);
        ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ind.addParameter(new Parameter("endDate", "End Date", Date.class));
        ind.addParameter(new Parameter("locationList", "List of Locations", Location.class));
        ind.setType(CohortIndicator.IndicatorType.COUNT);
        ind.setCohortDefinition(mappedCohort);
        return ind;
    }
}