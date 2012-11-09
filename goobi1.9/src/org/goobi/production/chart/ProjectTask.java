package org.goobi.production.chart;
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

public class ProjectTask implements IProjectTask {
	private String taskTitle;
	private Integer taskStepsCompleted;
	private Integer taskStepsMax;

	
	public ProjectTask(String title, Integer stepsCompleted, Integer stepsMax) {
		taskTitle = title;
		taskStepsCompleted = stepsCompleted;
		taskStepsMax = stepsMax;
		checkSizes();
	}

	public String getTitle() {
		return taskTitle;
	}

	public Integer getStepsCompleted() {
		return taskStepsCompleted;
	}

	public Integer getStepsMax() {
		return taskStepsMax;
	}

	public void setStepsCompleted(Integer stepsCompleted) {
		taskStepsCompleted = stepsCompleted;
	}

	public void setStepsMax(Integer stepsMax) {
		taskStepsMax = stepsMax;
	}

	private void checkSizes() {
		if (taskStepsCompleted > taskStepsMax) {
			taskStepsMax = taskStepsCompleted;
		}
	}
}
