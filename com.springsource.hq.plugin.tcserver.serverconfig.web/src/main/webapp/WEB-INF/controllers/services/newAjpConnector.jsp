<%--
  ~ Copyright (C) 2009-2015  Pivotal Software, Inc
  ~
  ~ This program is is free software; you can redistribute it and/or modify
  ~ it under the terms version 2 of the GNU General Public License as
  ~ published by the Free Software Foundation.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/WEB-INF/layouts/top.jsp" />

<div id="secondary-navigation">
  <jsp:include page="local-navigation.jsp" />
</div><!-- /secondary-navigation -->

<div id="content">
  &nbsp;
  <div id="breadcrumb">
    <spring:url value="/app/{settingsId}/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Home</a>
    &gt;
    <spring:url value="/app/{settingsId}/services/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Services</a>
    &gt;
    <spring:url value="/app/{settingsId}/services/{serviceId}/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
      <spring:param name="serviceId" value="${service.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}"><c:out value="${service.name}" /></a>
    &gt;
    <spring:url value="/app/{settingsId}/services/{serviceId}/connectors/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
      <spring:param name="serviceId" value="${service.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Connectors</a>
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    New AJP Connector for <c:out value="${service.name}" />
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-ConnectorProperties"
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  
  <spring:url value="/app/{settingsId}/services/{serviceId}/connectors/ajp-new/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
    <spring:param name="serviceId" value="${service.humanId}" />
  </spring:url>
  <form:form modelAttribute="ajpConnector" action="${url}" method="post">
    <jsp:include page="ajpConnector-inc.jsp" />
    <br />
    <c:if test="${!readOnly}">
      <input type="submit" class="spring-js-submit" value="Save"/>
    </c:if>
  </form:form>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
