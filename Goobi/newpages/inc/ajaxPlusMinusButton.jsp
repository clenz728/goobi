<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>

<a4j:commandLink reRender="auflistung" style="color:black" id="plusminusbutton">
	<h:graphicImage value="/newpages/images/plus.gif"
		style="margin-right:4px" rendered="#{!item.panelAusgeklappt}" />
	<h:graphicImage value="/newpages/images/minus.gif"
		style="margin-right:4px" rendered="#{item.panelAusgeklappt}" />
	<x:updateActionListener value="#{item.panelAusgeklappt?false:true}"
		property="#{item.panelAusgeklappt}" />
	<h:outputText value="#{item.titel}" />
</a4j:commandLink>
