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
    &gt;
    <spring:url value="/app/{settingsId}/services/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Services</a>
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    Create a New Service
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-GeneralServiceProperties"
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  
  <form:form modelAttribute="newService" method="post">
    <table class="bordered-table">
      <tr>
        <th colspan="2">Service Properties</th>
      </tr>
      <tr>
        <td><form:label path="name" cssClass="required">Name</form:label></td>
        <td>
          <form:input path="name" cssClass="spring-js-text spring-js-required" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesservicename" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
          <form:errors path="name" cssClass="error" />
        </td>
      </tr>
    </table>
    <br />
    <spring:nestedPath path="engine">
      <jsp:include page="engine-inc.jsp" />
      <br />
      <spring:nestedPath path="newHost">
        <jsp:include page="host-inc.jsp" />
      </spring:nestedPath>
    </spring:nestedPath>
    <br />
    <table class="bordered-table">
      <tr>
        <th>Extras</th>
      </tr>
      <tr>
        <td>
          <label>
            <form:checkbox path="httpConnector" cssClass="spring-js-boolean" />
            Create HTTP Connector
          </label>
        </td>
      </tr>
      <tr>
        <td>
          <label>
            <form:checkbox path="ajpConnector" cssClass="spring-js-boolean" />
            Create AJP Connector
          </label>
        </td>
      </tr>
      <tr>
        <td>
          <label>
            <form:checkbox path="logging" cssClass="spring-js-boolean" />
            Enable HTTP Access Logging
          </label>
        </td>
      </tr>
      <tr>
        <td>
          <label>
            <form:checkbox path="threadDiagnostics" cssClass="spring-js-boolean" />
            Enable Thread Diagnostics
          </label>
        </td>
      </tr>
    </table>
    <br />
    <c:if test="${!readOnly}">
      <input type="submit" class="spring-js-submit" value="Save"/>
    </c:if>
  </form:form>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
