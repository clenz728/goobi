package org.goobi.production.flow.statistics.hibernate;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of 
 * mass digitization.
 * 
 * Visit the websites for more information. 
 *   - http://gdz.sub.uni-goettingen.de 
 *   - http://www.intranda.com 
 * 
 * Copyright 2009, Center for Retrospective Digitization, Göttingen (GDZ),
 * 
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA 02111-1307 USA
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.goobi.production.flow.statistics.IDataSource;
import org.goobi.production.flow.statistics.IStatisticalQuestion;
import org.goobi.production.flow.statistics.enums.CalculationUnit;
import org.goobi.production.flow.statistics.enums.StatisticsMode;
import org.goobi.production.flow.statistics.enums.TimeUnit;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.intranda.commons.chart.renderer.HtmlTableRenderer;
import de.intranda.commons.chart.renderer.IRenderer;
import de.intranda.commons.chart.results.DataRow;
import de.intranda.commons.chart.results.DataTable;
import de.sub.goobi.Beans.Schritt;
import de.sub.goobi.helper.Helper;

/*****************************************************************************
 * Implementation of {@link IStatisticalQuestion}. 
 * Statistical Request with predefined Values in data Table
 * 
 * @author Steffen Hankiewicz
 ****************************************************************************/
public class StatQuestVolumeStatus implements IStatisticalQuestion {

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#getDataTables(org.goobi.production.flow.statistics.IDataSource)
	 */
	public List<DataTable> getDataTables(IDataSource dataSource) {

		IEvaluableFilter originalFilter;

		if (dataSource instanceof IEvaluableFilter) {
			originalFilter = (IEvaluableFilter) dataSource;
		} else {
			throw new UnsupportedOperationException("This implementation of IStatisticalQuestion needs an IDataSource for method getDataSets()");
		}

		//		Criteria crit = originalFilter.getCriteria();

		Criteria crit = Helper.getHibernateSession().createCriteria(Schritt.class);
		crit.add(Restrictions.or(Restrictions.eq("bearbeitungsstatus", Integer.valueOf(1)), Restrictions.like("bearbeitungsstatus", Integer.valueOf(2))));

		if (originalFilter instanceof UserDefinedFilter) {
			crit.createCriteria("prozess", "proz");
			crit.add(Restrictions.in("proz.id", originalFilter.getIDList()));
		}
		StringBuilder title = new StringBuilder(StatisticsMode.getByClassName(this.getClass()).getTitle());

		DataTable dtbl = new DataTable(title.toString());
		dtbl.setShowableInPieChart(true);
		DataRow dRow = new DataRow(Helper.getTranslation("count"));

		for (Object obj : crit.list()) {
			Schritt step = (Schritt) obj;
			String kurztitel = (step.getTitel().length() > 60 ? step.getTitel().substring(0, 60) + "..." : step.getTitel());
			dRow.addValue(kurztitel, dRow.getValue(kurztitel) + 1);
		}

		dtbl.addDataRow(dRow);
		List<DataTable> allTables = new ArrayList<DataTable>();

		//dtbl = dtbl.getDataTableInverted();
		dtbl.setUnitLabel(Helper.getTranslation("arbeitsschritt"));
		allTables.add(dtbl);
		return allTables;
	}

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#isRendererInverted(de.intranda.commons.chart.renderer.IRenderer)
	 */
	public Boolean isRendererInverted(IRenderer inRenderer) {
		return inRenderer instanceof HtmlTableRenderer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#setCalculationUnit(org.goobi.production.flow.statistics.enums.CalculationUnit)
	 */
	public void setCalculationUnit(CalculationUnit cu) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#setTimeUnit(org.goobi.production.flow.statistics.enums.TimeUnit)
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#getNumberFormatPattern()
	 */
	public String getNumberFormatPattern() {
		return "#";
	}

}
