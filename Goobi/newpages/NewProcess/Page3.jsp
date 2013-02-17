<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>


<%-- ######################################## 

							Kopie einer Prozessvorlage anlegen

	#########################################--%>

<html>
<f:view locale="#{SpracheForm.locale}">
	<%@include file="../inc/head.jsp"%>
	<body>

	<htm:table cellspacing="5" cellpadding="0" styleClass="layoutTable"
		align="center">
		<%@include file="../inc/tbl_Kopf.jsp"%>
		<htm:tr>
			<%@include file="../inc/tbl_Navigation.jsp"%>
			<htm:td valign="top" styleClass="layoutInhalt">

				<%-- ++++++++++++++++     Inhalt      ++++++++++++++++ --%>
				<h:form id="utid30">
					<%-- Breadcrumb --%>
					<h:panelGrid id="utid31" width="100%" columns="1"
						styleClass="layoutInhaltKopf">
						<h:panelGroup id="utid32">
							<h:commandLink id="utid33" value="#{msgs.startseite}"
								action="newMain" />
							<f:verbatim> &#8250;&#8250; </f:verbatim>
							<h:commandLink id="utid34" value="#{msgs.prozessverwaltung}"
								action="ProzessverwaltungAlle" />
							<f:verbatim> &#8250;&#8250; </f:verbatim>
							<h:outputText id="utid35"
								value="#{msgs.einenNeuenProzessAnlegen}" />
						</h:panelGroup>
					</h:panelGrid>

					<htm:table border="0" align="center" width="100%" cellpadding="15">
						<htm:tr>
							<htm:td>
								<htm:h3>
									<h:outputText id="utid36"
										value="#{msgs.einenNeuenProzessAnlegen}" />
								</htm:h3>

								<%-- globale Warn- und Fehlermeldungen --%>
								<h:messages id="utid37" globalOnly="true" errorClass="text_red"
									infoClass="text_blue" showDetail="true" showSummary="true"
									tooltip="true" />

								<%-- ===================== Eingabe der Details ====================== --%>
								<htm:table cellpadding="3" cellspacing="0" width="100%"
									styleClass="eingabeBoxen">

									<htm:tr>
										<htm:td styleClass="eingabeBoxen_row1" colspan="2">
											<h:outputText id="utid38" value="#{msgs.nextStep}" />
										</htm:td>
									</htm:tr>

									<htm:tr>
										<htm:td styleClass="eingabeBoxen_row2" colspan="2">

											<h:panelGrid columns="1" cellpadding="4">

												<h:commandLink id="utid21"
													action="#{ProzesskopieForm.downloadDocket}">
													<h:graphicImage id="utid24" alt="/newpages/images/buttons/laufzettel_wide.png"
														value="/newpages/images/buttons/laufzettel_wide.png"
														style="vertical-align:middle" />
													<h:outputText value="#{msgs.laufzettelDrucken}" />
												</h:commandLink>

												<h:commandLink id="utid20"
													action="#{ProzesskopieForm.Prepare}">
													<h:graphicImage id="utid25"
														alt="/newpages/images/buttons/star_blue.gif"
														value="/newpages/images/buttons/star_blue.gif"
														style="vertical-align:middle" />
													<h:outputText value="#{msgs.weiterenVorgangAnlegen}" />
												</h:commandLink>

												<h:commandLink id="utid22"
													action="ProzessverwaltungBearbeiten">
													<x:updateActionListener
														property="#{ProzessverwaltungForm.myProzess}"
														value="#{ProzesskopieForm.prozessKopie}" />
														<x:updateActionListener value="" property="#{ProzessverwaltungForm.modusBearbeiten}"/>
													<h:graphicImage id="utid23"
														alt="/newpages/images/buttons/edit_20.gif"
														value="/newpages/images/buttons/edit_20.gif"
														style="vertical-align:middle" />

													<h:outputText value="#{msgs.denErzeugtenBandOeffnen}" />
												</h:commandLink>

											</h:panelGrid>
										</htm:td>

									</htm:tr>
								</htm:table>

								<%-- ===================== // Eingabe der Details ====================== --%>

							</htm:td>
						</htm:tr>
					</htm:table>
				</h:form>
				<%-- ++++++++++++++++    // Inhalt      ++++++++++++++++ --%>

			</htm:td>
		</htm:tr>
		<%@include file="../inc/tbl_Fuss.jsp"%>
	</htm:table>

	</body>
</f:view>

</html>
