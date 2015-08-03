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
    Push All Changes to Server
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerConfiguration.html#settings-save" 
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  
  <spring:url value="/app/{settingsId}/save/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" method="put">
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
          Are you sure you want to <strong>push all changes</strong> for this configuration to the server?  
          The existing server configuration will be overwritten with these settings.  
          Please verify all the changes you have made before writing the changes to the server. 
          <br />
          <br />
		  <c:if test="${!readOnly}">
		    <input type="submit" value="Push Changes to Server"/>
		  </c:if>
        </td>
      </tr>
    </table>
  </form:form>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
