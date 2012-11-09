package org.goobi.production.flow.statistics;

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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import de.intranda.commons.chart.renderer.CSVRenderer;
import de.intranda.commons.chart.renderer.ChartRenderer;
import de.intranda.commons.chart.renderer.ExcelRenderer;
import de.intranda.commons.chart.renderer.HtmlTableRenderer;
import de.intranda.commons.chart.renderer.IRenderer;
import de.intranda.commons.chart.renderer.PieChartRenderer;
import de.intranda.commons.chart.results.DataTable;
import de.sub.goobi.config.ConfigMain;

public class StatisticsRenderingElement implements Serializable {

	private static final long serialVersionUID = 9211752003070422596L;
	private IStatisticalQuestion myQuestion;
	private DataTable dataTable;
	private HtmlTableRenderer htmlTableRenderer;
	private CSVRenderer csvRenderer;
	private ExcelRenderer excelRenderer;
	private String localImagePath;
	private String imageUrl;
	private static final Logger logger = Logger.getLogger(StatisticsRenderingElement.class);

	public StatisticsRenderingElement(DataTable inDataTable, IStatisticalQuestion inQuestion) {
		dataTable = inDataTable;
		myQuestion = inQuestion;
	}

	public void createRenderer(Boolean inShowAverage) {
		/*
		 * -------------------------------- create image path
		 * --------------------------------
		 */
		localImagePath = ConfigMain.getTempImagesPathAsCompleteDirectory();

		/* create html renderer */
		createHtmlRenderer();
		/* create chart */
		createChart(inShowAverage);

	}

	/*************************************************************************************
	 * generate html presentation of datatable
	 *************************************************************************************/
	private void createHtmlRenderer() {
		htmlTableRenderer = new HtmlTableRenderer();
		csvRenderer = new CSVRenderer();
		excelRenderer = new ExcelRenderer();
		if (myQuestion.isRendererInverted(htmlTableRenderer)) {
			htmlTableRenderer.setDataTable(dataTable.getDataTableInverted());
			csvRenderer.setDataTable(dataTable.getDataTableInverted());
			excelRenderer.setDataTable(dataTable.getDataTableInverted());
		} else {
			htmlTableRenderer.setDataTable(dataTable);
			csvRenderer.setDataTable(dataTable);
			excelRenderer.setDataTable(dataTable);
		}
		htmlTableRenderer.setFormatPattern(myQuestion.getNumberFormatPattern());
		csvRenderer.setFormatPattern(myQuestion.getNumberFormatPattern());
		excelRenderer.setFormatPattern(myQuestion.getNumberFormatPattern());
	}

	/*************************************************************************************
	 * generate chart at defined folder of datatable
	 *************************************************************************************/
	private void createChart(Boolean inShowAverage) {
		imageUrl = System.currentTimeMillis() + ".png";

		IRenderer renderer = null;
		if (dataTable.isShowableInPieChart()) {
			renderer = new PieChartRenderer();
		} else {
			ChartRenderer crenderer = new ChartRenderer();
			crenderer.setShowMeanValues(inShowAverage);
			renderer = crenderer;
		}
		if (myQuestion.isRendererInverted(renderer)) {
			renderer.setDataTable(dataTable.getDataTableInverted());
		} else {
			renderer.setDataTable(dataTable);
		}
		File outputfile = new File(localImagePath + imageUrl);
		BufferedImage image = (BufferedImage) renderer.getRendering();
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			logger.error(e);
		}

	}

	/*************************************************************************************
	 * Getter for dataTable
	 * 
	 * @return the dataTable
	 *************************************************************************************/
	public DataTable getDataTable() {
		return dataTable;
	}

	/*************************************************************************************
	 * Getter for htmlTableRenderer
	 * 
	 * @return the htmlTableRenderer
	 *************************************************************************************/
	public HtmlTableRenderer getHtmlTableRenderer() {
		return htmlTableRenderer;
	}

	/*************************************************************************************
	 * Getter for title
	 * 
	 * @return the title
	 *************************************************************************************/
	public String getTitle() {
		return dataTable.getName() + " " + dataTable.getSubname();
	}

	/*************************************************************************************
	 * Getter for imageUrl
	 * 
	 * @return the imageUrl
	 *************************************************************************************/
	public String getImageUrl() {
		return imageUrl;
	}

	public CSVRenderer getCsvRenderer() {
		return csvRenderer;
	}

	public ExcelRenderer getExcelRenderer() {
		return excelRenderer;
	}

}
