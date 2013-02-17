<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<%-- ===================== GROSSE Vorlagen- BOX IM GELADENEN PROZESS ====================== --%>

<htm:h4 style="margin-top:15">
	<h:outputText value="#{msgs.vorlagen}" />
</htm:h4>

<x:dataTable id="vorlagen" styleClass="standardTable" width="100%"
	cellspacing="1px" cellpadding="1px" headerClass="standardTable_Header"
	rowClasses="standardTable_Row1,standardTable_Row2"
	columnClasses="standardTable_ColumnCentered,standardTable_Column,standardTable_ColumnCentered,standardTable_ColumnCentered"
	var="item" value="#{ProzessverwaltungForm.myProzess.vorlagenList}"
	rowIndexVar="index">

	<%-- ===================== Nr ====================== --%>
	<h:column>
		<f:facet name="header">
			<h:outputText value="#{msgs.nr}" />
		</f:facet>
		<h:outputText value="#{index+1}" />
	</h:column>
	<%-- ===================== // Nr ====================== --%>

	<%-- ===================== Eigenschaften ====================== --%>
	<h:column>
		<f:facet name="header">
			<h:outputText value="#{msgs.eigenschaften}" />
		</f:facet>
		<x:dataTable value="#{item.eigenschaften}" var="prop">
			<h:column>
				<h:outputText value="#{prop.titel}:" style="color:grey" />
			</h:column>
			<h:column>
				<h:outputText value="#{prop.wert}" />
			</h:column>
		</x:dataTable>
	</h:column>
	<%-- ===================== // Eigenschaften ====================== --%>

	<h:column
		rendered="#{(LoginForm.maximaleBerechtigung == 1) || (LoginForm.maximaleBerechtigung == 2)}">
		<f:facet name="header">
			<h:outputText value="#{msgs.auswahl}" />
		</f:facet>
		<%-- Bearbeiten-Schaltknopf --%>
		<h:commandLink action="ProzessverwaltungBearbeitenVorlage"
			title="#{msgs.vorlageBearbeiten}">
			<h:graphicImage value="/newpages/images/buttons/goInto.gif" />
			<x:updateActionListener
				property="#{ProzessverwaltungForm.myVorlageReload}" value="#{item}" />
		</h:commandLink>
	</h:column>

</x:dataTable>

<%-- Neu-Schaltknopf --%>
<h:commandLink action="#{ProzessverwaltungForm.VorlageNeu}"
	rendered="#{(LoginForm.maximaleBerechtigung == 1) || (LoginForm.maximaleBerechtigung == 2)}"
	value="#{msgs.vorlageHinzufuegen}" title="#{msgs.vorlageHinzufuegen}">
</h:commandLink>
