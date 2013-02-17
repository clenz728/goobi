<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>

<%-- ########################################

                                 Formular f�r neue Metadaten

#########################################--%>
<h:panelGroup rendered="#{Metadaten.modusHinzufuegen}">
	<htm:h3 style="margin-top:10px">
		<h:outputText value="#{msgs.metadatenBearbeiten}" />
	</htm:h3>
	<htm:table id="render" cellpadding="3" cellspacing="0"
		style="width:540px" styleClass="eingabeBoxen">
		<htm:tr>
			<htm:td styleClass="eingabeBoxen_row1">
				<h:outputText value="#{msgs.metadatenBearbeiten}" />
			</htm:td>
		</htm:tr>
		<htm:tr>
			<htm:td styleClass="eingabeBoxen_row2">


				<h:panelGrid id="grid1" columns="2">
					<%-- typ --%>
					<h:outputLabel for="mytyp" value="#{msgs.typ}" />

					<h:panelGroup>
						<h:selectOneMenu id="mytyp" style="width: 400px;margin-right:15px"
							value="#{Metadaten.tempTyp}" required="true"
							onchange="loadValue()">
							<f:selectItems value="#{Metadaten.addableMetadataTypes}" />
							<a4j:support event="onmouseup" requestDelay="1" />
						</h:selectOneMenu>
						<x:message for="mytyp" style="color: red"
							replaceIdWithLabel="true" />

						<a4j:jsFunction name="loadValue" reRender="formular2:render">
							<%--       formular2:myInput,formular2:myTextarea,formular2:mySelect1,formular2:mySelect">
				       --%>
						</a4j:jsFunction>

					</h:panelGroup>

					<h:outputLabel for="myValues" value="#{msgs.wert}" />

					<h:panelGroup>
						<x:div id="myValues">

							<h:inputTextarea id="myTextarea"
								value="#{Metadaten.metadatum.value}" immediate="true"
								rendered="#{(Metadaten.outputType == 'textarea')}"
								onchange="styleAnpassen(this)" styleClass="metadatenInput"
								style="width: 350px;height: 45px;">
							</h:inputTextarea>
							<h:inputText id="myInput" value="#{Metadaten.metadatum.value}"
								onchange="styleAnpassen(this)"
								rendered="#{(Metadaten.outputType == 'input')}"
								styleClass="metadatenInput" style="width: 350px;" />
							<h:selectManyListbox id="mySelect"
								value="#{Metadaten.metadatum.selectedItems}"
								rendered="#{(Metadaten.outputType == 'select')}"
								onchange="styleAnpassen(this)" immediate="true">
								<f:selectItems value="#{Metadaten.metadatum.items}" />
							</h:selectManyListbox>
							<h:selectOneMenu id="mySelect1"
								value="#{Metadaten.metadatum.selectedItem}"
								rendered="#{(Metadaten.outputType == 'select1')}"
								onchange="styleAnpassen(this)" immediate="true">
								<f:selectItems value="#{Metadaten.metadatum.items}" />
							</h:selectOneMenu>
							<h:outputText id="myOutput" value="#{Metadaten.metadatum.value}"
							
								rendered="#{(Metadaten.outputType == 'readonly')}"
								styleClass="metadatenInput" style="width: 350px;" />
						</x:div>
					</h:panelGroup>
				</h:panelGrid>
			</htm:td>
		</htm:tr>

		<htm:tr>
			<htm:td styleClass="eingabeBoxen_row3">
				<h:commandButton action="#{Metadaten.Abbrechen}"
					value="#{msgs.abbrechen}" immediate="false">
					<x:updateActionListener value="huhu"
						property="#{Metadaten.metadatum.value}"></x:updateActionListener>
				</h:commandButton>

				<x:commandButton id="absenden" forceId="true" type="submit"
					action="#{Metadaten.Speichern}"
					value="#{msgs.dieAenderungenSpeichern}"></x:commandButton>
			</htm:td>
		</htm:tr>

	</htm:table>
</h:panelGroup>
