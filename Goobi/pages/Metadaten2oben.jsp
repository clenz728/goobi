<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:view locale="#{SpracheForm.locale}">
	<%@include file="/newpages/inc/head.jsp"%>
	<body>
	
	<h:form id="formularOben">
		<htm:table width="100%" style="margin-top:3px" align="center"
			border="0px">

			<htm:tr valign="top">
				<htm:td colspan="2">
					<htm:table width="100%" styleClass="layoutKopf"
						style="#{HelperForm.applicationHeaderBackground}" cellpadding="0"
						cellspacing="0" border="0">
						<htm:tr valign="top">
							<htm:td width="20%" height="48">
								<h:commandLink action="#{Metadaten.goMain}" target="_parent">
									<h:graphicImage value="#{HelperForm.applicationLogo}" />
								</h:commandLink>
							</htm:td>
							<htm:td valign="middle" align="center">
								<h:outputText style="#{HelperForm.applicationTitleStyle}"
									value="#{Metadaten.myProzess.titel}" />
								<htm:noscript>
									<h:outputText style="color: red;font-weight: bold;"
										value="#{msgs.keinJavascript}" />
								</htm:noscript>
							</htm:td>
							<htm:td valign="middle" align="right">
								<h:panelGrid id="grid1" columns="2" cellpadding="0" cellspacing="0">
									<h:commandLink value="#{msgs.treelevel} " action="Metadaten3links"
										target="links" styleClass="metadataHeaderLinks">
										<x:updateActionListener
											value="#{Metadaten.treeProperties.showtreelevel?false:true}"
											property="#{Metadaten.treeProperties.showtreelevel}" />
									</h:commandLink>
									<h:commandLink value="#{msgs.treeTitle} " action="Metadaten3links" style="padding-left: 5px;"
										target="links" styleClass="metadataHeaderLinks">
										<x:updateActionListener
											value="#{Metadaten.treeProperties.showtitle?false:true}"
											property="#{Metadaten.treeProperties.showtitle}" />
									</h:commandLink>
									<h:commandLink value="#{msgs.treePageNumber} "
										action="Metadaten3links" target="links" styleClass="metadataHeaderLinks">
										<x:updateActionListener
											value="#{Metadaten.treeProperties.showfirstpagenumber?false:true}"
											property="#{Metadaten.treeProperties.showfirstpagenumber}" />
									</h:commandLink>
									<h:commandLink value="#{msgs.treeExpand} " style="padding-left: 5px;"
										action="#{Metadaten.TreeExpand}" target="links"
										styleClass="metadataHeaderLinks">
										<x:updateActionListener
											value="#{Metadaten.treeProperties.fullexpanded?false:true}"
											property="#{Metadaten.treeProperties.fullexpanded}" />
									</h:commandLink>
									<h:commandLink value="#{msgs.stickyImage} "
										action="Metadaten2rechts" target="rechts"
										styleClass="metadataHeaderLinks">
										<x:updateActionListener
											value="#{Metadaten.treeProperties.imageSticky?false:true}"
											property="#{Metadaten.treeProperties.imageSticky}" />
									</h:commandLink>
									<h:outputText />
								</h:panelGrid>
							</htm:td>
							<htm:td width="105px" align="right" valign="middle"
								style="padding:3px">
								<x:inputText rendered="false" id="treeReload" forceId="true" />
								<h:commandLink rendered="false" id="treeReloadButton"
									value="TreeReloaden" action="Metadaten3links" target="links" />
								<h:commandLink action="#{SpracheForm.SpracheUmschalten}"
									title="deutsche Version" target="rechts">
									<h:graphicImage value="/newpages/images/flag_de_ganzklein.gif" />
									<f:param name="locale" value="de" />
									<f:param name="ziel" value="Metadaten2rechts" />
								</h:commandLink>
								<h:commandLink action="#{SpracheForm.SpracheUmschalten}"
									title="english version" target="rechts">
									<h:graphicImage value="/newpages/images/flag_en_ganzklein.gif" />
									<f:param name="locale" value="en" />
									<f:param name="ziel" value="Metadaten2rechts" />
								</h:commandLink>
								<h:commandLink action="#{SpracheForm.SpracheUmschalten}"
									title="spanish version" target="rechts">
									<h:graphicImage value="/newpages/images/flag_es_ganzklein.gif" />
									<f:param name="locale" value="es" />
									<f:param name="ziel" value="Metadaten2rechts" />
								</h:commandLink>
								<h:commandLink action="#{SpracheForm.SpracheUmschalten}"
									title="english version" target="rechts" rendered="false">
									<h:graphicImage value="/newpages/images/flag_ru_ganzklein.gif" />
									<f:param name="locale" value="ru" />
									<f:param name="ziel" value="Metadaten2rechts" />
								</h:commandLink>
							</htm:td>
						</htm:tr>
					</htm:table>
				</htm:td>
			</htm:tr>

		</htm:table>
	</h:form>



	</body>
</f:view>
</html>
