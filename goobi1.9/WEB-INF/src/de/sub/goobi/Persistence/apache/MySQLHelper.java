package de.sub.goobi.Persistence.apache;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 * 			- http://digiverso.com 
 * 			- http://www.intranda.com
 * 
 * Copyright 2012, intranda GmbH, Göttingen
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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import de.sub.goobi.Beans.ProjectFileGroup;
import de.sub.goobi.Beans.Regelsatz;

public class MySQLHelper {

	private static final int MAX_TRIES_NEW_CONNECTION = 5;
	private static final int TIME_FOR_CONNECTION_VALID_CHECK = 5;

	private static final Logger logger = Logger.getLogger(MySQLHelper.class);

	private static MySQLHelper helper = new MySQLHelper();
	private ConnectionManager cm = null;

	private MySQLHelper() {
		SqlConfiguration config = SqlConfiguration.getInstance();
		this.cm = new ConnectionManager(config);
	}

	public Connection getConnection() throws SQLException {

		Connection connection = this.cm.getDataSource().getConnection();

		if (connection.isValid(TIME_FOR_CONNECTION_VALID_CHECK)) {
			return connection;
		}

		for (int i = 0; i < MAX_TRIES_NEW_CONNECTION; i++) {

			logger.warn("Connection failed: Trying to get new connection. Attempt:" + i);

			connection = this.cm.getDataSource().getConnection();

			if (connection.isValid(TIME_FOR_CONNECTION_VALID_CHECK)) {
				return connection;
			}
		}

		logger.warn("Connection failed: Trying to get a connection from a new ConnectionManager");
		SqlConfiguration config = SqlConfiguration.getInstance();
		this.cm = new ConnectionManager(config);
		connection = this.cm.getDataSource().getConnection();

		if (connection.isValid(TIME_FOR_CONNECTION_VALID_CHECK)) {
			return connection;
		}

		logger.error("Connection failed!");

		return connection;
	}

	public static void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}

	public static MySQLHelper getInstance() {
		return helper;
	}

	public static List<StepObject> getStepsForProcess(int processId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM schritte WHERE ProzesseID = " + processId);
		sql.append(" ORDER BY Reihenfolge ASC");
		try {
			logger.debug(sql.toString());
			List<StepObject> ret = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToStepObjectListHandler);
			return ret;
		} finally {
			closeConnection(connection);
		}
	}

	public static List<Property> getProcessPropertiesForProcess(int processId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM prozesseeigenschaften WHERE prozesseID = " + processId);
		try {
			logger.debug(sql.toString());
			List<Property> answer = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToProcessPropertyListHandler);
			return answer;
		} finally {
			closeConnection(connection);
		}
	}

	public static List<Property> getTemplatePropertiesForProcess(int processId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM vorlageneigenschaften WHERE vorlageneigenschaften.vorlagenID = (SELECT VorlagenID FROM vorlagen WHERE ProzesseID = "
				+ processId);
		try {
			logger.debug(sql.toString());
			List<Property> answer = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToTemplatePropertyListHandler);
			return answer;
		} finally {
			closeConnection(connection);
		}
	}

	public static List<Property> getProductPropertiesForProcess(int processId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM werkstueckeeigenschaften WHERE werkstueckeeigenschaften.werkstueckeID = (SELECT werkstueckeID FROM werkstuecke WHERE ProzesseID = "
				+ processId + ")");
		try {
			logger.debug(sql.toString());
			List<Property> answer = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToProductPropertyListHandler);
			return answer;
		} finally {
			closeConnection(connection);
		}
	}

	public static ProcessObject getProcessObjectForId(int processId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM prozesse WHERE ProzesseID = " + processId);
		try {
			logger.debug(sql.toString());
			ProcessObject answer = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToProcessHandler);
			return answer;
		} finally {
			closeConnection(connection);
		}

	}

	public static Regelsatz getRulesetForId(int rulesetId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM metadatenkonfigurationen WHERE MetadatenKonfigurationID = " + rulesetId);
		try {
			logger.debug(sql.toString());
			Regelsatz ret = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToRulesetHandler);
			return ret;
		} finally {
			closeConnection(connection);
		}
	}

	public static StepObject getStepByStepId(int stepId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM schritte WHERE SchritteID = " + stepId);
		// sql.append(" ORDER BY Reihenfolge ASC");
		try {
			logger.debug(sql.toString());
			StepObject ret = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToStepObjectHandler);
			return ret;
		} finally {
			closeConnection(connection);
		}
	}

	public static List<String> getScriptsForStep(int stepId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM schritte WHERE SchritteID = " + stepId);
		try {
			logger.debug(sql.toString());
			List<String> ret = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToScriptsHandler);
			return ret;
		} finally {
			closeConnection(connection);
		}
	}

	public static Map<String, String> getScriptMapForStep(int stepId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM schritte WHERE SchritteID = " + stepId);
		try {
			logger.debug(sql.toString());
			Map<String, String> ret = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToScriptMapHandler);
			return ret;
		} finally {
			closeConnection(connection);
		}
	}

	public int updateStep(StepObject step) throws SQLException {
		int ret = -1;
		Connection connection = helper.getConnection();
		try {
			QueryRunner run = new QueryRunner();
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE schritte SET Titel = '" + step.getTitle() + "', ");
			sql.append("Reihenfolge = '" + step.getReihenfolge() + "', ");
			sql.append("Bearbeitungsstatus = '" + step.getBearbeitungsstatus() + "', ");
			if (step.getBearbeitungszeitpunkt() != null) {
				sql.append("BearbeitungsZeitpunkt = '" + new Timestamp(step.getBearbeitungszeitpunkt().getTime()) + "', ");
			}
			if (step.getBearbeitungsbeginn() != null) {
				sql.append("BearbeitungsBeginn = '" + new Timestamp(step.getBearbeitungsbeginn().getTime()) + "', ");
			}
			if (step.getBearbeitungsende() != null) {
				sql.append("BearbeitungsEnde = '" + new Timestamp(step.getBearbeitungsende().getTime()) + "', ");
			}
			sql.append("BearbeitungsBenutzerID = '" + step.getBearbeitungsbenutzer() + "', ");
			sql.append("edittype = '" + step.getEditType() + "', ");
			sql.append("typAutomatisch = " + step.isTypAutomatisch());

			sql.append(" WHERE SchritteID = " + step.getId() + ";");
			logger.debug("saving step: " + sql.toString());
			run.update(connection, sql.toString());
			// logger.debug(sql);
			ret = step.getId();
			return ret;
		} finally {
			closeConnection(connection);
		}
	}

	public void addHistory(Date date, double order, String value, int type, int processId) throws SQLException {
		Connection connection = helper.getConnection();
		Timestamp datetime = new Timestamp(date.getTime());

		try {
			QueryRunner run = new QueryRunner();
			String propNames = "numericValue, stringvalue, type, date, processId";
			String propValues = "'" + order + "','" + value + "','" + type + "','" + datetime + "','" + processId + "'";
			String sql = "INSERT INTO " + "history" + " (" + propNames + ") VALUES (" + propValues + ")";
			logger.trace("added history event " + sql);
			run.update(connection, sql);
		} finally {
			closeConnection(connection);
		}
	}

	public void updateProcessStatus(String value, int processId) throws SQLException {
		Connection connection = helper.getConnection();
		try {
			QueryRunner run = new QueryRunner();
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE prozesse SET sortHelperStatus = '" + value + "' WHERE ProzesseID = " + processId + ";");
			logger.debug(sql.toString());
			run.update(connection, sql.toString());
		} finally {
			closeConnection(connection);
		}
	}

	public void updateImages(Integer numberOfFiles, int processId) throws SQLException {
		Connection connection = helper.getConnection();
		try {
			QueryRunner run = new QueryRunner();
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE prozesse SET sortHelperImages = '" + numberOfFiles + "' WHERE ProzesseID = " + processId + ";");
			logger.debug(sql.toString());
			run.update(connection, sql.toString());
		} finally {
			closeConnection(connection);
		}
	}

	public void updateProcessLog(String logValue, int processId) throws SQLException {
		Connection connection = helper.getConnection();
		try {
			QueryRunner run = new QueryRunner();
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE prozesse SET wikifield = '" + logValue + "' WHERE ProzesseID = " + processId + ";");
			logger.debug(sql.toString());
			run.update(connection, sql.toString());
		} finally {
			closeConnection(connection);
		}
	}

	public static ProjectObject getProjectObjectById(int projectId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM projekte WHERE ProjekteID = " + projectId);
		try {
			logger.debug(sql.toString());
			ProjectObject answer = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToProjectHandler);
			return answer;
		} finally {
			closeConnection(connection);
		}
	}

	public static List<ProjectFileGroup> getFilegroupsForProjectId(int projectId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM projectfilegroups WHERE ProjekteID = " + projectId);
		try {
			logger.debug(sql.toString());
			List<ProjectFileGroup> answer = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToProjectFilegroupListHandler);
			return answer;

		} finally {
			closeConnection(connection);
		}
	}

	public static List<String> getFilterForUser(int userId) throws SQLException {
		Connection connection = helper.getConnection();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM benutzereigenschaften WHERE Titel = '_filter' AND BenutzerID = " + userId);
		try {
			logger.debug(sql.toString());
			List<String> answer = new QueryRunner().query(connection, sql.toString(), MySQLUtils.resultSetToFilterListtHandler);
			return answer;
		} finally {
			closeConnection(connection);
		}
	}

	public static void addFilterToUser(int userId, String filterstring) throws SQLException {
		Connection connection = helper.getConnection();
		Timestamp datetime = new Timestamp(new Date().getTime());
		try {
			QueryRunner run = new QueryRunner();
			String propNames = "Titel, Wert, IstObligatorisch, DatentypenID, Auswahl, creationDate, BenutzerID";
			String propValues = "'_filter','" + filterstring + "'," + false + ",'" + 5 + "'," + null + ",'" + datetime + "','" + userId + "'";
			String sql = "INSERT INTO " + "benutzereigenschaften" + " (" + propNames + ") VALUES (" + propValues + ")";
			logger.debug(sql.toString());
			run.update(connection, sql);
		} finally {
			closeConnection(connection);
		}
	}

	public static void removeFilterFromUser(int userId, String filterstring) throws SQLException {
		Connection connection = helper.getConnection();
		try {
			QueryRunner run = new QueryRunner();
			String sql = "DELETE FROM benutzereigenschaften WHERE Titel = '_filter' AND Wert = '" + filterstring + "'";
			logger.debug(sql.toString());
			run.update(connection, sql);
		} finally {
			closeConnection(connection);
		}
	}

	public static List<Integer> getStepIds(String query) throws SQLException {
		Connection connection = helper.getConnection();
		try {
			return new QueryRunner().query(connection, query, MySQLUtils.resultSetToIntegerListHandler);
		} finally {
			closeConnection(connection);
		}
	}

	public static int getCountOfProcessesWithRuleset(int rulesetId) throws SQLException {
		Connection connection = helper.getConnection();
		String query = "select count(ProzesseID) from prozesse where MetadatenKonfigurationID = " + rulesetId;
		try {
			return new QueryRunner().query(connection, query, MySQLUtils.resultSetToIntegerHandler);
		} finally {
			closeConnection(connection);
		}
	}
	
	public static int getCountOfProcessesWithDocket(int docketId) throws SQLException {
		Connection connection = helper.getConnection();
		String query = "select count(ProzesseID) from prozesse where  docketID= " + docketId;
		try {
			return new QueryRunner().query(connection, query, MySQLUtils.resultSetToIntegerHandler);
		} finally {
			closeConnection(connection);
		}
	}
}
