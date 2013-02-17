<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>

<%-- ######################################## 

							All dockets

	#########################################--%>

<a4j:keepAlive beanName="DocketForm" />
<html>
<f:view locale="#{SpracheForm.locale}">
	<%@include file="inc/head.jsp"%>
	<body>
		<htm:table styleClass="headTable" cellspacing="0" cellpadding="0" style="padding-left:5px;padding-right:5px;margin-top:5px;">
			<%@include file="inc/tbl_Kopf.jsp"%>
		</htm:table>
		<htm:table cellspacing="5" cellpadding="0" styleClass="layoutTable"
			align="center">
			<htm:tr>
				<%@include file="inc/tbl_Navigation.jsp"%>
				<htm:td valign="top" styleClass="layoutInhalt">

					<%-- ++++++++++++++++     Inhalt      ++++++++++++++++ --%>
					<h:form id="docketform">
						<%-- Breadcrumb --%>
						<h:panelGrid id="id0" width="100%" columns="1"
							styleClass="layoutInhaltKopf">
							<h:panelGroup id="id1">
								<h:commandLink id="id2" value="#{msgs.startseite}"
									action="newMain" />
								<f:verbatim> &#8250;&#8250; </f:verbatim>
								<h:outputText id="id3" value="#{msgs.dockets}" />
							</h:panelGroup>
						</h:panelGrid>

						<htm:table border="0" align="center" width="100%" cellpadding="15">
							<htm:tr>
								<htm:td>

									<%-- �?berschrift --%>
									<htm:h3>
										<h:outputText id="id4" value="#{msgs.dockets}" />
									</htm:h3>

									<%-- Neu-Schaltknopf --%>
									<h:commandLink id="id5" action="#{DocketForm.Neu}"
										immediate="true"
										rendered="#{(LoginForm.maximaleBerechtigung == 1) || (LoginForm.maximaleBerechtigung == 2)}">
										<h:outputText id="id6" value="#{msgs.createNewDocket}" />
									</h:commandLink>

									<%-- globale Warn- und Fehlermeldungen --%>
									<h:messages id="id7" globalOnly="true" errorClass="text_red"
										infoClass="text_blue" showDetail="true" showSummary="true"
										tooltip="true" />

									<%-- Datentabelle --%>
									<x:dataTable id="id8" styleClass="standardTable" width="100%"
										cellspacing="1px" cellpadding="1px"
										headerClass="standardTable_Header"
										rowClasses="standardTable_Row1,standardTable_Row2"
										columnClasses="standardTable_Column,standardTable_Column,standardTable_ColumnCentered,standardTable_ColumnCentered" style="margin-top: 10px;"
										var="item" value="#{DocketForm.page.listReload}">

										<h:column id="id9">
											<f:facet name="header">
												<h:outputText id="id10" value="#{msgs.titel}" />
											</f:facet>
											<h:outputText id="id11" value="#{item.name}" />
										</h:column>

										<h:column id="id12">
											<f:facet name="header">
												<h:outputText id="id13" value="#{msgs.datei}" />
											</f:facet>
											<h:outputText id="id14" value="#{item.file}" />
										</h:column>

										
										<h:column id="id19"
											rendered="#{(LoginForm.maximaleBerechtigung == 1) || (LoginForm.maximaleBerechtigung == 2)}">
											<f:facet name="header">
												<h:outputText id="id20" value="#{msgs.auswahl}" />
											</f:facet>
											<%-- Bearbeiten-Schaltknopf --%>
											<h:commandLink id="id21" action="DocketEdit"
												title="#{msgs.editDocket}">
												<h:graphicImage id="id22"
													value="/newpages/images/buttons/edit.gif" />
												<x:updateActionListener
													property="#{DocketForm.myDocket}" value="#{item}" />
											</h:commandLink>
										</h:column>

									</x:dataTable>
									<h:commandLink id="id52" action="#{DocketForm.Neu}"
										immediate="true"
										rendered="#{((LoginForm.maximaleBerechtigung == 1) || (LoginForm.maximaleBerechtigung == 2)) && (DocketForm.page.totalResults > LoginForm.myBenutzer.tabellengroesse)}">
										<h:outputText id="id62" value="#{msgs.createNewDocket}" />
									</h:commandLink>
									<htm:table width="100%" border="0">
										<htm:tr valign="top">
											<htm:td align="left">

											</htm:td>
											<htm:td align="center">
												<%-- ===================== Datascroller für die Ergebnisse ====================== --%>
												<x:aliasBean alias="#{mypage}"
													value="#{DocketForm.page}">
													<jsp:include page="/newpages/inc/datascroller.jsp" />
												</x:aliasBean>
												<%-- ===================== // Datascroller für die Ergebnisse ====================== --%>
											</htm:td>
										</htm:tr>
									</htm:table>
								</htm:td>
							</htm:tr>
						</htm:table>
					</h:form>
					<%-- ++++++++++++++++    // Inhalt      ++++++++++++++++ --%>

				</htm:td>
			</htm:tr>
			<%@include file="inc/tbl_Fuss.jsp"%>
		</htm:table>

	</body>
</f:view>

</html>
