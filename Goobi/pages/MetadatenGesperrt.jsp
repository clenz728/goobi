<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:view locale="#{SpracheForm.locale}">
	<%@include file="/newpages/inc/head.jsp"%>
	<body>


	<h:form id="lockform">
		<h:outputText
			value="#{msgs.MetadataTimeout}"
			style="font-size: 12px;color: red" />
		<htm:br />
		<htm:br />
		<h:commandLink action="#{Metadaten.goZurueck}" target="_parent">
			<h:outputText value="#{msgs.goBack}" style="font-size: 11px" />
		</h:commandLink>

	</h:form>

	</body>
</f:view>
</html>

