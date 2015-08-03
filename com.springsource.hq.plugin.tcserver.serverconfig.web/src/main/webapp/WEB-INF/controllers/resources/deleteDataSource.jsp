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
    <spring:url value="/app/{settingsId}/resources/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Resources</a>
    &gt;
    <spring:url value="/app/{settingsId}/resources/jdbc/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">JDBC Data Sources</a>
    &gt;
    <spring:url value="/app/{settingsId}/resources/jdbc/{dataSourceId}/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
      <spring:param name="dataSourceId" value="${dataSourceId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}"><c:out value="${dataSource.general.jndiName}" /></a>
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    Delete Data Source
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html"
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  
  <spring:url value="/app/{settingsId}/resources/jdbc/{dataSourceId}/delete/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
    <spring:param name="dataSourceId" value="${dataSource.humanId}" />
  </spring:url>
  <form:form action="${url}" method="delete">
    <table class="bordered-table">
      <tr>
        <th>&nbsp;</th>
      </tr>
      <tr>
        <td>
          Are you sure you want to delete <strong><c:out value="${dataSource.general.jndiName}" /></strong>?
          <br />
          <br />
	      <c:if test="${!readOnly}">
	          <input type="submit" value="Delete" />
	      </c:if>
        </td>
      </tr>
    </table>
  </form:form>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
