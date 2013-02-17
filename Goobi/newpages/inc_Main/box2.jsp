<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<htm:table cellpadding="3" cellspacing="0" width="100%"
	styleClass="main_statistikboxen" style="margin-top:30px">
	<htm:tr>
		<htm:td styleClass="main_statistikboxen_row1">
			<h:outputText value="#{msgs.statistikPersoenlich}" />
		</htm:td>
	</htm:tr>
	<htm:tr>
		<htm:td styleClass="main_statistikboxen_row2">
			<h:panelGrid width="100%" columns="2" columnClasses="columnLinks,columnRechts">
				<h:outputText value="#{msgs.aktuelleSchritte}:" />
				<h:outputText value="#{StatistikForm.anzahlAktuelleSchritte}" />
				<h:outputText value="#{msgs.statusOffen}:" />
				<h:outputText value="#{StatistikForm.anzahlAktuelleSchritteOffen}" />
				<h:outputText value="#{msgs.statusInBearbeitung}:" />
				<h:outputText value="#{StatistikForm.anzahlAktuelleSchritteBearbeitung}" />
			</h:panelGrid>
		</htm:td>
	</htm:tr>
</htm:table>