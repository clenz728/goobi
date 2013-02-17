package org.goobi.production.api.property.xmlbasedprovider;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. - http://gdz.sub.uni-goettingen.de - http://www.intranda.com
 * 
 * Copyright 2009, Center for Retrospective Digitization, Göttingen (GDZ),
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Linking this library statically or dynamically with other modules is making a combined work based on this library. Thus, the terms and conditions
 * of the GNU General Public License cover the whole combination. As a special exception, the copyright holders of this library give you permission to
 * link this library with independent modules to produce an executable, regardless of the license terms of these independent modules, and to copy and
 * distribute the resulting executable under terms of your choice, provided that you also meet, for each linked independent module, the terms and
 * conditions of the license of that module. An independent module is a module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but you are not obliged to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 * 
 */
import de.sub.goobi.Beans.Projekt;
import de.sub.goobi.Beans.Prozess;
import de.sub.goobi.Beans.Schritt;
import de.sub.goobi.Beans.Vorlage;
import de.sub.goobi.Beans.Werkstueck;

/**
 * simple POJO class to generate PropertyTemplate for a IGoobiEntity
 * 
 * @author rsehr
 * 
 */
public class Status {

	private String project;
	private String process;
	private String step;
	private String product;
	private String productionResource;

	public void setProject(String project) {
		this.project = project;
	}

	public String getProject() {
		return this.project;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcess() {
		return this.process;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getStep() {
		return this.step;
	}

	public void setProduct(String digitalDocument) {
		this.product = digitalDocument;
	}

	public String getProduct() {
		return this.product;
	}

	public void setProductionResource(String original) {
		this.productionResource = original;
	}

	public String getProductionResource() {
		return this.productionResource;
	}

	public static Status getProjectStatus(Projekt inEntity) {
		Status myStatus = new Status();
		myStatus.setProject(inEntity.getTitel());
		return myStatus;
	}

	public static Status getProcessStatus(Prozess inEntity) {
		Status myStatus = new Status();
		myStatus.setProject(inEntity.getProjekt().getTitel());
		myStatus.setProcess(inEntity.getTitel());
		return myStatus;
	}

	public static Status getStepStatus(Schritt inEntity) {
		Status myStatus = new Status();
		myStatus.setProject(inEntity.getProzess().getProjekt().getTitel());
		myStatus.setProcess(inEntity.getProzess().getTitel());
		myStatus.setStep(inEntity.getTitel());
		return myStatus;
	}

	public static Status getResourceStatusFromEntity(Vorlage inEntity) {
		Status myStatus = new Status();
		myStatus.setProject(inEntity.getProzess().getProjekt().getTitel());
		myStatus.setProcess(inEntity.getProzess().getTitel());
		if (inEntity.getId() != null) {
			myStatus.setProductionResource(String.valueOf(inEntity.getId()));
		} else  {
			myStatus.setProductionResource("0");
		}
		return myStatus;
	}

	public static Status getProductStatusFromEntity(Werkstueck inEntity) {
		Status myStatus = new Status();
		myStatus.setProject(inEntity.getProzess().getProjekt().getTitel());
		myStatus.setProcess(inEntity.getProzess().getTitel());
		if (inEntity.getId() != null) {
			myStatus.setProduct(String.valueOf(inEntity.getId()));
		} else  {
			myStatus.setProduct("0");
		}
		return myStatus;
	}


}
