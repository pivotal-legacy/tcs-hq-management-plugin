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
    Revert to Previous Configuration
  </h1>
  
  <spring:url value="/app/{settingsId}/revertToPreviousConfiguration/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" method="delete">
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
          Are you sure you want to <strong>revert</strong> to previous configuration settings?
		  <c:if test="${!readOnly}">  
            Any local changes will be lost.
		  </c:if>
          <br />
          <br />
		  <input type="submit" value="Revert"/>
        </td>
      </tr>
    </table>
  </form:form>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
