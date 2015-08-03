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

<form:hidden path="id" />
<table class="bordered-table">
  <tr>
    <th colspan="2">General</th>
  </tr>
  <tr>
    <td><form:label path="general.jndiName" cssClass="required">JNDI Name</form:label></td>
    <td>
      <form:input path="general.jndiName" cssClass="spring-js-text spring-js-required" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourcegeneraljndiName" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="general.jndiName" cssClass="error" />
    </td>
  </tr>
</table>
<br />
<table class="bordered-table">
  <tr>
    <th colspan="2">Connection</th>
  </tr>
  <tr>
    <td><form:label path="connection.driverClassName" cssClass="required">Driver Class Name</form:label></td>
    <td>
      <form:input path="connection.driverClassName" cssClass="spring-js-text spring-js-required spring-js-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectiondriverClassName" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="connection.driverClassName" cssClass="error" />
    </td>
    <spring:bind path="connection.driverClassName">
      <script type="text/javascript">
        ams.putId("id.connection.driverClassName", "${fn:escapeXml(status.expression)}");
      </script>
    </spring:bind>
  </tr>
  <tr>
    <td><form:label path="connection.url" cssClass="required">URL</form:label></td>
    <td>
      <form:input path="connection.url" cssClass="spring-js-text spring-js-required spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionurl" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="connection.url" cssClass="error" />
    </td>
    <spring:bind path="connection.url">
      <script type="text/javascript">
        ams.putId("id.connection.url", "${fn:escapeXml(status.expression)}");
      </script>
    </spring:bind>
  </tr>
  <tr>
    <td><form:label path="connection.username" cssClass="required">Username</form:label></td>
    <td>
      <form:input path="connection.username" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionusername" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="connection.username" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="connection.obscuredPassword" cssClass="required">Password</form:label></td>
    <td>
      <form:password path="connection.obscuredPassword" cssClass="spring-js-text" showPassword="true"/>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionpassword" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="connection.obscuredPassword" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="connection.connectionProperties">Connection Properties</form:label></td>
    <td>
      <form:textarea path="connection.connectionProperties" cssClass="spring-js-textarea spring-js-extra-wide" rows="3" cols="40" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionconnectionPro..." 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="connection.connectionProperties" cssClass="error" />
    </td>
  </tr>
</table>

<script type="text/javascript">
  var jdbcDriversDataUrl = "<c:url value="/resources/scripts/jdbcDriversData.js" />";
</script>
<script type="text/javascript" src="<c:url value="/resources/scripts/jdbcDrivers.js" />"></script>
