<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<%-- ######################################## 

							Prozess bearbeiten

	#########################################--%>

<html>
<f:view locale="#{SpracheForm.locale}">
	<%@include file="/newpages/inc/head.jsp"%>
	<body onload="txtAutomatischAnzeigen();">

	<htm:table cellspacing="5" cellpadding="0" styleClass="layoutTable"
		align="center">
		<%@include file="/newpages/inc/tbl_Kopf.jsp"%>
		<htm:tr>
			<%@include file="/newpages/inc/tbl_Navigation.jsp"%>
			<htm:td valign="top" styleClass="layoutInhalt">

				<%-- ++++++++++++++++     Inhalt      ++++++++++++++++ --%>
				<h:form id="stepform" onkeypress="ifEnterClick(event, 'stepform:absenden');">
					<%-- Breadcrumb --%>
					<h:panelGrid columns="1" styleClass="layoutInhaltKopf">
						<h:panelGroup>
							<h:commandLink value="#{msgs.startseite}" action="newMain" />
							<f:verbatim> &#8250;&#8250; </f:verbatim>
							<h:commandLink value="#{msgs.prozessverwaltung}"
								action="ProzessverwaltungAlle" />
							<f:verbatim> &#8250;&#8250; </f:verbatim>
							<h:commandLink value="#{msgs.prozessDetails}"
								action="ProzessverwaltungBearbeiten">
								<x:updateActionListener
									property="#{ProzessverwaltungForm.reload}" value="" />

							</h:commandLink>
							<f:verbatim> &#8250;&#8250; </f:verbatim>
							<h:outputText value="#{msgs.schrittDetails}" />
						</h:panelGroup>
					</h:panelGrid>

					<htm:table border="0" align="center" width="100%" cellpadding="15">
						<htm:tr>
							<htm:td>
								<htm:h3>
									<h:outputText value="#{msgs.schrittHinzufuegen}"
										rendered="#{ProzessverwaltungForm.mySchritt.id == null}" />
									<h:outputText value="#{msgs.schrittDetails}"
										rendered="#{ProzessverwaltungForm.mySchritt.id != null}" />
								</htm:h3>

								<%-- globale Warn- und Fehlermeldungen --%>
								<h:messages globalOnly="true" errorClass="text_red"
									infoClass="text_blue" showDetail="true" showSummary="true"
									tooltip="true" />

								<%-- Schrittdetails --%>
								<%@include
									file="/newpages/inc_Prozessverwaltung/schritt_box_Details.jsp"%>

								<%-- Schritteigenschaften --%>
								<%@include
									file="/newpages/inc_Prozessverwaltung/schritt_box_Eigenschaften.jsp"%>

								<%-- Benutzer-Berechtigungen --%>
								<%@include
									file="/newpages/inc_Prozessverwaltung/schritt_box_Benutzer.jsp"%>
								<%@include
									file="/newpages/inc_Prozessverwaltung/schritt_box_Benutzergruppen.jsp"%>
								<%-- --%>
								<h:commandButton action="#{NavigationForm.Reload}"
									style="display:none" />
							</htm:td>
						</htm:tr>
					</htm:table>
				</h:form>
				<%-- ++++++++++++++++    // Inhalt      ++++++++++++++++ --%>

			</htm:td>
		</htm:tr>
		<%@include file="/newpages/inc/tbl_Fuss.jsp"%>
	</htm:table>

	</body>
</f:view>
</html>
