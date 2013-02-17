<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://www.jenia.org/jsf/dynamic" prefix="jd"%>
<%@ taglib uri="http://sourceforge.net/projects/jsf-comp/easysi" prefix="si"%>


<h:panelGroup rendered="#{AktuelleSchritteForm.batchHelper.currentStep.bearbeitungsbenutzer.id == LoginForm.myBenutzer.id}">


	<%-- ++++++++++++++++     // Import      ++++++++++++++++ --%>
	<h:form id="actionform">
		<%-- ++++++++++++++++     Action      ++++++++++++++++ --%>
		<htm:table cellpadding="3" cellspacing="0" width="100%" styleClass="eingabeBoxen" style="margin-top:20px">
			<htm:tr>
				<htm:td styleClass="eingabeBoxen_row1">
					<h:outputText value="#{msgs.moeglicheAktionen}" />
				</htm:td>
			</htm:tr>
			<htm:tr>
				<htm:td styleClass="eingabeBoxen_row2">
					<h:panelGrid columns="1">


						<x:dataList var="script" value="#{AktuelleSchritteForm.batchHelper.scriptnames}" layout="unorderedList">
							<h:commandLink id="action3" action="#{AktuelleSchritteForm.batchHelper.executeScript}" title="#{script}">
								<x:updateActionListener property="#{AktuelleSchritteForm.batchHelper.script}" value="#{script}" />
								<h:graphicImage value="/newpages/images/buttons/admin4b.gif" style="margin-right:3px;vertical-align:middle" />
								<h:outputText value="#{msgs.scriptAusfuehren}: #{script}" />
							</h:commandLink>
						</x:dataList>


						<h:outputText style="back-color:blue; color: red; font-weight: bold;" rendered="#{AktuelleSchritteForm.batchHelper.currentStep.typExportDMS }"
							value="#{msgs.timeoutWarningDMS}" />

						<h:commandLink id="action9" rendered="#{AktuelleSchritteForm.batchHelper.currentStep.typExportDMS}"
							action="#{AktuelleSchritteForm.batchHelper.ExportDMS}" title="#{msgs.importDms}">
							<h:graphicImage value="/newpages/images/buttons/dms.png" style="margin-right:3px;vertical-align:middle" />
							<h:outputText value="#{msgs.importDms}" />
						</h:commandLink>







						<%-- Schritt zurückgeben an vorherige Station für Korrekturzwecke --%>
						<h:panelGroup>
							<jd:hideableController for="korrektur" id="korrekturswitcher" title="#{msgs.korrekturmeldungAnVorherigeStationSenden}">
								<h:graphicImage value="/newpages/images/buttons/step_back_20px.gif" style="margin-right:3px;vertical-align:middle" />
								<h:outputText value="#{msgs.korrekturmeldungAnVorherigeStationSenden}" />
							</jd:hideableController>

							<jd:hideableArea id="korrektur" saveState="view">
								<h:panelGrid columns="2" style="margin-left:40px;" id="grid3" rowClasses="top" columnClasses="standardTable_Column,standardTable_ColumnRight">
									<h:outputText value="#{msgs.zurueckZuArbeitsschritt}" />
									<h:selectOneMenu style="width:350px" value="#{AktuelleSchritteForm.batchHelper.myProblemStep}">
										<f:selectItems value="#{AktuelleSchritteForm.batchHelper.previousStepsForProblemReporting}" />
										<%-- <si:selectItems value="#{AktuelleSchritteForm.batchHelper.previousStepsForProblemReporting}" var="step1"
											itemLabel="#{step1.titelMitBenutzername}" itemValue="#{step1.id}" />--%>
									</h:selectOneMenu>
									<h:outputText value="#{msgs.bemerkung}" />
									<h:inputTextarea style="width:350px;height:80px" value="#{AktuelleSchritteForm.batchHelper.problemMessage}" />
									<h:outputText value="" />
									<h:panelGroup>
										<h:commandLink id="action130" action="#{AktuelleSchritteForm.batchHelper.ReportProblemForSingle}" title="#{msgs.korrekturmeldungSenden}"
											onclick="if (!confirm('#{msgs.wirklichAusfuehren}?')) return">
											<h:outputText value="#{msgs.korrekturmeldungSendenSingle}" />
										</h:commandLink>
										<h:outputText value=" | "/>
										<h:commandLink id="action131" action="#{AktuelleSchritteForm.batchHelper.ReportProblemForAll}" title="#{msgs.korrekturmeldungSenden}"
											onclick="if (!confirm('#{msgs.wirklichAusfuehren}?')) return">
											<h:outputText value="#{msgs.korrekturmeldungSendenForAll}" />
										</h:commandLink>
									</h:panelGroup>
								</h:panelGrid>
							</jd:hideableArea>
						</h:panelGroup>

						<%-- Schritt weitergeben an nachfolgende Station für KorrekturBehobenZwecke --%>
						<h:panelGroup rendered="#{AktuelleSchritteForm.batchHelper.currentStep.prioritaet>9}">
							<jd:hideableController for="solution" id="solutionswitcher" title="#{msgs.meldungUeberProblemloesungAnNachchfolgendeStationSenden}">
								<h:graphicImage value="/newpages/images/buttons/step_for_20px.gif" style="margin-right:3px;vertical-align:middle" />
								<h:outputText value="#{msgs.meldungUeberProblemloesungAnNachchfolgendeStationSenden}" />
							</jd:hideableController>

							<jd:hideableArea id="solution" saveState="view">
								<h:panelGrid columns="2" style="margin-left:40px;" rowClasses="top" id="grid1" columnClasses="standardTable_Column,standardTable_ColumnRight">
									<h:outputText value="#{msgs.weiterZuArbeitsschritt}" />
									<h:selectOneMenu style="width:350px" id="select1" value="#{AktuelleSchritteForm.batchHelper.mySolutionStep}">
										<f:selectItems value="#{AktuelleSchritteForm.batchHelper.nextStepsForProblemSolution}" />
									
										<%-- <si:selectItems value="#{AktuelleSchritteForm.batchHelper.nextStepsForProblemSolution}" var="step2"
											itemLabel="#{step2.titelMitBenutzername}" itemValue="#{step2.id}" />--%>
									</h:selectOneMenu>
									<h:outputText value="#{msgs.bemerkung}" />
									<h:inputTextarea style="width:350px;height:80px" id="input1" value="#{AktuelleSchritteForm.batchHelper.solutionMessage}" />
									<h:outputText value="" />
									<h:panelGroup>
										<h:commandLink id="action140" action="#{AktuelleSchritteForm.batchHelper.SolveProblemForSingle}"
											title="#{msgs.meldungUeberProblemloesungSenden}" onclick="if (!confirm('#{msgs.wirklichAusfuehren}?')) return">
											<h:outputText value="#{msgs.meldungUeberProblemloesungSendenSingle}" />
										</h:commandLink>
										<h:outputText value=" | "/>
										<h:commandLink id="action141" action="#{AktuelleSchritteForm.batchHelper.SolveProblemForAll}"
											title="#{msgs.meldungUeberProblemloesungSenden}" onclick="if (!confirm('#{msgs.wirklichAusfuehren}?')) return">
											<h:outputText value="#{msgs.meldungUeberProblemloesungSendenForAll}" />
										</h:commandLink>
									</h:panelGroup>
								</h:panelGrid>
							</jd:hideableArea>
						</h:panelGroup>
--%>

						<%-- Bearbeitung abbrechen-Schaltknopf --%>
						<h:commandLink id="action11" action="#{AktuelleSchritteForm.batchHelper.BatchDurchBenutzerZurueckgeben}"
							title="#{msgs.bearbeitungDiesesSchrittesAbgeben}" onclick="if (!confirm('#{msgs.bearbeitungDiesesSchrittesWirklichAbgeben}')) return">
							<h:graphicImage value="/newpages/images/buttons/cancel3.gif" style="margin-right:3px;vertical-align:middle" />
							<h:outputText value="#{msgs.bearbeitungDiesesSchrittesAbgeben}" />
						</h:commandLink>
						<%-- Abschliessen-Schaltknopf --%>
						<h:commandLink id="action15" action="#{AktuelleSchritteForm.batchHelper.BatchDurchBenutzerAbschliessen}"
							title="#{msgs.diesenSchrittAbschliessen}" onclick="if (!confirm('#{msgs.diesenSchrittAbschliessen}?')) return">
							<h:graphicImage value="/newpages/images/buttons/ok.gif" style="margin-right:3px;vertical-align:middle" />
							<h:outputText value="#{msgs.diesenSchrittAbschliessen}" />
						</h:commandLink>

					</h:panelGrid>
				</htm:td>
			</htm:tr>

		</htm:table>
		<%-- ++++++++++++++++     // Action      ++++++++++++++++ --%>
	</h:form>
</h:panelGroup>
