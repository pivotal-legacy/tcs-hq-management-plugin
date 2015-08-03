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

<div id="content-no-nav">
  &nbsp;
  <div id="breadcrumb">
    <spring:url value="/app/{settingsId}/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Home</a>
  </div>
  
  <h1>
    Restart Server
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerConfiguration.html#restart" 
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  
  <spring:url value="/app/{settingsId}/restart/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" method="post">
    <table class="bordered-table">
      <tr>
        <th>&nbsp;</th>
      </tr>
      <c:if test="${not empty error}">
        <tr>
          <td>
            <span class="error"><c:out value="${error}" /></span>
          </td>
        </tr>
      </c:if>
      <tr>
        <td>
          Are you sure you want to <strong>restart the server</strong>?  
          The server must be restarted before any configuration changes will take effect.
          <c:if test="${changePending}">
            <spring:url value="/app/{settingsId}/save/" var="url">
              <spring:param name="settingsId" value="${settings.humanId}" />
            </spring:url>
          	<br />
            <br />
            <strong>NOTE:</strong>
          	Changes have been made to the server configuration that have not been written out to the server yet.
          	In order to have any effect on the server, these changes must be <a href="${fn:escapeXml(url)}">saved</a> before the restart.
          </c:if> 
          <br />
          <br />
		  <c:if test="${!readOnly}">
		    <input type="submit" value="Restart Server"/>
		  </c:if>
        </td>
      </tr>
    </table>
  </form:form>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
