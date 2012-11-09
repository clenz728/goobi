package de.sub.goobi.Forms;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 * 			- http://digiverso.com 
 * 			- http://www.intranda.com
 * 
 * Copyright 2011, intranda GmbH, Göttingen
 * 
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
 */
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.File;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import de.sub.goobi.Beans.Regelsatz;
import de.sub.goobi.Persistence.RegelsatzDAO;
import de.sub.goobi.Persistence.apache.ProcessManager;
import de.sub.goobi.config.ConfigMain;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.Page;
import de.sub.goobi.helper.exceptions.DAOException;

@ManagedBean(name="RegelsaetzeForm") 
@SessionScoped
public class RegelsaetzeForm extends BasisForm {
	private static final long serialVersionUID = -445707928042517243L;
	private Regelsatz myRegelsatz = new Regelsatz();
	private RegelsatzDAO dao = new RegelsatzDAO();
	private static final Logger logger = Logger.getLogger(RegelsaetzeForm.class);

	public String Neu() {
		this.myRegelsatz = new Regelsatz();
		return "ruleset_edit";
	}

	public String Speichern() {
		try {
			if (hasValidRulesetFilePath(myRegelsatz, ConfigMain.getParameter("RegelsaetzeVerzeichnis"))) {
				dao.save(myRegelsatz);
				return "ruleset_all";
			} else {
				Helper.setFehlerMeldung("RulesetNotFound");
				return "";
			}
		} catch (DAOException e) {
			Helper.setFehlerMeldung("fehlerNichtSpeicherbar", e.getMessage());
			logger.error(e);
			return "";
		}
	}

	private boolean hasValidRulesetFilePath(Regelsatz r, String pathToRulesets) {
		File rulesetFile = new File(pathToRulesets + r.getDatei());
		return rulesetFile.exists();
	}

	public String Loeschen() {
		try {
			if (hasAssignedProcesses(myRegelsatz)) {
				Helper.setFehlerMeldung("RulesetInUse");
				return "";
			} else {
				dao.remove(myRegelsatz);
			}
		} catch (DAOException e) {
			Helper.setFehlerMeldung("fehlerNichtLoeschbar", e.getMessage());
			return "";
		}
		return "ruleset_all";
	}

	private boolean hasAssignedProcesses(Regelsatz r) {
		Integer number = ProcessManager.getNumberOfProcessesWithRuleset(r.getId());
		if (number != null && number > 0) {
			return true;
		}
		return false;
	}

	public String FilterKein() {
		try {
			// HibernateUtil.clearSession();
			Session session = Helper.getHibernateSession();
			// session.flush();
			session.clear();
			Criteria crit = session.createCriteria(Regelsatz.class);
			crit.addOrder(Order.asc("titel"));
			this.page = new Page(crit, 0);
		} catch (HibernateException he) {
			Helper.setFehlerMeldung("fehlerBeimEinlesen", he.getMessage());
			return "";
		}
		return "ruleset_all";
	}

	public String FilterKeinMitZurueck() {
		FilterKein();
		return this.zurueck;
	}

	/*
	 * ##################################################### ##################################################### ## ## Getter und Setter ##
	 * ##################################################### ####################################################
	 */

	public Regelsatz getMyRegelsatz() {
		return this.myRegelsatz;
	}

	public void setMyRegelsatz(Regelsatz inPreference) {
		Helper.getHibernateSession().clear();
		this.myRegelsatz = inPreference;
	}
}
