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
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    JDBC Data Sources
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-tcServerJDBCReference"
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  <jsp:include page="/WEB-INF/layouts/messages.jsp" />
  
  <table class="bordered-table">
    <tr>
      <th>Name</th>
      <th>Action</th>
    </tr>
    <c:if test="${empty dbcpDataSources and empty tomcatDataSources}">
      <tr>
        <td colspan="2">no data sources found</td>
      </tr>
    </c:if>
    <c:forEach items="${tomcatDataSources}" var="dataSource">
      <tr>
        <td>
          <spring:url value="/app/{settingsId}/resources/jdbc/tomcat/{dataSourceId}/" var="url">
            <spring:param name="settingsId" value="${settings.humanId}" />
            <spring:param name="dataSourceId" value="${dataSource.humanId}" />
          </spring:url>
          <a href="${fn:escapeXml(url)}"><c:out value="${dataSource.general.jndiName}" /></a>
        </td>
        <td>
	    <c:if test="${!readOnly}">
          <spring:url value="/app/{settingsId}/resources/jdbc/{dataSourceId}/delete/" var="url">
            <spring:param name="settingsId" value="${settings.humanId}" />
            <spring:param name="dataSourceId" value="${dataSource.humanId}" />
          </spring:url>
          <a href="${fn:escapeXml(url)}">Delete</a>
        </c:if>
        </td>
      </tr>
    </c:forEach>
    <c:forEach items="${dbcpDataSources}" var="dataSource">
      <tr>
        <td>
          <spring:url value="/app/{settingsId}/resources/jdbc/dbcp/{dataSourceId}/" var="url">
            <spring:param name="settingsId" value="${settings.humanId}" />
            <spring:param name="dataSourceId" value="${dataSource.humanId}" />
          </spring:url>
          <a href="${fn:escapeXml(url)}"><c:out value="${dataSource.general.jndiName}" /></a>
        </td>
        <td>
	    <c:if test="${!readOnly}">
          <spring:url value="/app/{settingsId}/resources/jdbc/{dataSourceId}/delete/" var="url">
            <spring:param name="settingsId" value="${settings.humanId}" />
            <spring:param name="dataSourceId" value="${dataSource.humanId}" />
          </spring:url>
          <a href="${fn:escapeXml(url)}">Delete</a>
        </c:if>
        </td>
      </tr>
    </c:forEach>
  </table>
  <br />
  <c:if test="${!readOnly}">
  <spring:url value="/app/{settingsId}/resources/jdbc/tomcat-new/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <a href="${fn:escapeXml(url)}">Create new Tomcat High Concurrency Data Source</a>
  <br />
  <spring:url value="/app/{settingsId}/resources/jdbc/dbcp-new/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <a href="${fn:escapeXml(url)}">Create new DBCP Data Source</a>
  </c:if>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
