package de.sub.goobi.forms;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 *     		- http://www.goobi.org
 *     		- http://launchpad.net/goobi-production
 * 		    - http://gdz.sub.uni-goettingen.de
 * 			- http://www.intranda.com
 * 			- http://digiverso.com 
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

import de.sub.goobi.config.ConfigMain;

@ManagedBean(name = "NavigationForm")
@SessionScoped
public class NavigationForm {
	private String aktuell = "0";
	private boolean showHelp = false;
	private boolean showExpertView = false;
	private boolean showSidebar = true;

	public String getAktuell() {
		return this.aktuell;
	}

	public void setAktuell(String aktuell) {
		this.aktuell = aktuell;
	}

	public String Reload() {
		return "";
	}

	public String JeniaPopupCloseAction() {
		return "jeniaClosePopupFrameWithAction";
	}

	public String BenutzerBearbeiten() {
		return "BenutzerBearbeiten";
	}

	/**
	 * 
	 * @return true if show_taskmanager in file GoobiConfig.properties is =true
	 */
	public Boolean getShowTaskManager() {
		return ConfigMain.getBooleanParameter("show_taskmanager", false);
	}

	public Boolean getShowModuleManager() {
		return ConfigMain.getBooleanParameter("show_modulmanager", false);
	}

	public boolean isShowHelp() {
		return showHelp;
	}

	public void setShowHelp(boolean showHelp) {
		this.showHelp = showHelp;
	}
	
	public boolean isShowExpertView() {
		return showExpertView;
	}
	
	public void setShowExpertView(boolean showExpertView) {
		this.showExpertView = showExpertView;
	}
	
	public boolean isShowSidebar() {
		return showSidebar;
	}
	public void setShowSidebar(boolean showSidebar) {
		this.showSidebar = showSidebar;
	}
}
